import com.acme.db.business.dao.AppDao
import com.acme.db.business.entity.App
import com.acme.db.business.entity.Org
import com.acme.db.business.entity.OrgApp
import com.acme.db.sync.entity.IntegrationSync
import com.acme.db.util.withSession
import com.acme.kafka.consumer.IntegrationApiConsumer
import com.acme.kafka.consumer.IntegrationDbWriterConsumer
import java.time.LocalDateTime
import java.util.UUID
import net.mguenther.kafka.junit.EmbeddedKafkaCluster
import net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.common.serialization.UUIDSerializer
import org.apache.kafka.common.serialization.UUIDDeserializer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.service.ServiceRegistry
import java.sql.DatabaseMetaData

fun initDb(): Pair<UUID, UUID> {
    // Create org for Acme
    val org = Org(
        id = UUID.randomUUID(),
        name = "acme",
        url = "http://www.acme.com"
    )

    // Create the app for Intercom
    val app = App(
        id = UUID.randomUUID(),
        name = "intercom"
    )

    val orgApp = OrgApp(
        id = UUID.randomUUID(),
        orgId = org.id,
        appId = app.id,
        installationDateTime = LocalDateTime.now()
    )

    withSession { session ->
        session.save(org)
        session.save(app)
        session.save(orgApp)
    }

    println(AppDao.getApp(app.id))
    println(AppDao.getInstallationListByOrgId(org.id, app.id))

    return Pair(org.id, app.id)
}

fun runSyncWithKafka(orgId: UUID, appId: UUID) {
    // Configure and start the embedded Kafka cluster
    val clusterConfig = EmbeddedKafkaClusterConfig.defaultClusterConfig()
    val cluster = EmbeddedKafkaCluster(clusterConfig)
    cluster.start()

    // Create the KafkaProducer and KafkaConsumer
    val producerProps = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, cluster.brokerList)
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UUIDSerializer::class.java)
    }

    val adminClient = AdminClient.create(producerProps)
    val newTopics = listOf(NewTopic("api-request", 1, 1),
        NewTopic("db-writer", 1, 1))
    adminClient.createTopics(newTopics).all().get()

    val producer = KafkaProducer<String, UUID>(producerProps)

    val apiConsumerProps = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, cluster.brokerList)
        put(ConsumerConfig.GROUP_ID_CONFIG, "api-request-group")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer::class.java)
    }

    val dbWriterConsumerProps = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, cluster.brokerList)
        put(ConsumerConfig.GROUP_ID_CONFIG, "db-writer-group")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer::class.java)
    }

    val kafkaApiConsumer = KafkaConsumer<String, UUID>(apiConsumerProps)
    kafkaApiConsumer.subscribe(listOf("api-request"))

    val kafkaDbWriterConsumer = KafkaConsumer<String, UUID>(dbWriterConsumerProps)
    kafkaDbWriterConsumer.subscribe(listOf("db-writer"))

    // Create and start the consumers
    val apiConsumer = IntegrationApiConsumer(kafkaApiConsumer, producer)
    val dbWriterConsumer = IntegrationDbWriterConsumer(kafkaDbWriterConsumer)

    // Start the consumers in separate threads
    Thread(apiConsumer).start()
    Thread(dbWriterConsumer).start()

    // Create a brand new sync and send it over to api-request
    val integrationSyncId = withSession { session ->
        session.save(
            IntegrationSync(
                id = UUID.randomUUID(),
                orgId = orgId,
                appId = appId,
                startTime = LocalDateTime.now(),
                endTime = null,
                status = 0,
                totalRecords = 0
            )
        ) as UUID
    }

    Thread.sleep(5000)
    val record = ProducerRecord("api-request", orgId.toString(), integrationSyncId)

    // Send the producer record.
    producer.send(record) { metadata, exception ->
        if (exception != null) {
            // Handle the exception. This is usually log4j, but I am just printing it out here
            println("Error while producing: ${exception.message}")
        } else {
            // Handle the exception. This is usually log4j, but I am just printing it out here
            println("Produced record to topic ${metadata.topic()} partition ${metadata.partition()} at offset ${metadata.offset()}")
        }
    }

    // Don't forget to stop the cluster at the end
    // cluster.stop()
}

fun debugTables() {
    val registry: ServiceRegistry = StandardServiceRegistryBuilder()
        .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
        .applySetting("hibernate.connection.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
        .applySetting("hibernate.connection.username", "sa")
        .applySetting("hibernate.connection.password", "")
        .build()

    val sessionFactory = MetadataSources(registry).buildMetadata().buildSessionFactory()

    sessionFactory.openSession().use { session ->
        val connection = session.doReturningWork { it.metaData.connection }
        val metaData: DatabaseMetaData = connection.metaData
        val tables = metaData.getTables(null, null, "%", arrayOf("TABLE"))
        while (tables.next()) {
            val tableName = tables.getString("TABLE_NAME")
            println("Table: $tableName")
            val columns = metaData.getColumns(null, null, tableName, null)
            while (columns.next()) {
                val columnName = columns.getString("COLUMN_NAME")
                val columnType = columns.getString("TYPE_NAME")
                println("Column: $columnName, Type: $columnType")
            }
            println("-------------------------------")
        }
    }
}

fun main(args: Array<String>) {
    val (orgId, appId) = initDb()
    debugTables()
    runSyncWithKafka(orgId, appId)
}