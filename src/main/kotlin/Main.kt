import com.acme.db.business.dao.AppDao
import com.acme.db.business.entity.App
import com.acme.db.business.entity.Org
import com.acme.db.business.entity.OrgApp
import com.acme.db.util.withSession
import com.acme.kafka.consumer.IntegrationApiConsumer
import com.acme.kafka.consumer.IntegrationDbWriterConsumer
import java.time.LocalDateTime
import java.util.UUID
import net.mguenther.kafka.junit.EmbeddedKafkaCluster
import net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig
import net.mguenther.kafka.junit.EmbeddedKafkaConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.common.serialization.UUIDSerializer
import org.apache.kafka.common.serialization.UUIDDeserializer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import java.util.*

fun initDb() {
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
}

fun initKafka() {
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
    kafkaDbWriterConsumer.subscribe(listOf("db-writer-group"))

    // Create and start the consumers
    val apiConsumer = IntegrationApiConsumer(kafkaApiConsumer, producer)
    val dbWriterConsumer = IntegrationDbWriterConsumer(kafkaDbWriterConsumer)

    // Start the consumers in separate threads
    Thread(apiConsumer).start()
    Thread(dbWriterConsumer).start()

    // Do some work...

    // Don't forget to stop the cluster at the end
    // cluster.stop()
}

fun main(args: Array<String>) {
    initDb()
    initKafka()
}