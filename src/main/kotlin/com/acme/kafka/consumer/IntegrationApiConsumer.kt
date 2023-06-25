package com.acme.kafka.consumer

import com.acme.db.sync.dao.ApiCallDao
import com.acme.db.sync.entity.ApiCall
import com.acme.db.util.withSession
import com.acme.integration.ticketing.intercom.IntercomTicketingSystemApi
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

/**
 * Kafka enabled consumer to ingest messages and run api requests. This consumer is responsible to saving the response
 * contents to a file and calling the DBWriter to write data to the database.
 */
class IntegrationApiConsumer(private val consumer: KafkaConsumer<String, UUID>, private val producer: KafkaProducer<String, UUID>) :
    Runnable {

    fun writeResponseToFile(syncId: UUID, inputStream: InputStream): String {
        val path = Paths.get("$BASE_PATH/$syncId")
        val parentDir = path.parent

        if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir)
        }

        val fileOutputStream = FileOutputStream(path.toFile())
        fileOutputStream.use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        return path.toAbsolutePath().toString()
    }

    private fun toNext(syncId: UUID, appDbEntryId: UUID) {
        val record = ProducerRecord("db-writer", syncId.toString(), appDbEntryId)

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
    }

    override fun run() {


        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))

            for (record in records) {
                val syncId = record.value() ?: continue // this is really an error condition

                println("Received syncId $syncId, processing")
                // I am just going to hard code this here, the actual api class should be create dynamically depending
                // on the type of app so that this code can remain generic
                val apiCaller = IntercomTicketingSystemApi()

                // Create new entry in the db to record the call into the api
                val apiDbEntryId = withSession { session ->
                    session.save(
                        ApiCall(
                            id = UUID.randomUUID(),
                            syncId = syncId,
                            startTime = LocalDateTime.now()
                        )
                    ) as UUID
                }

                var backOffTime = 1000L
                var retries = 0

                while (retries < MAX_RETRIES) {
                    try {
                        val filePath = writeResponseToFile(syncId, apiCaller.callApi().entity as InputStream)
                        // Now update the file location
                        ApiCallDao.updateComplete(apiDbEntryId, filePath)
                        toNext(syncId, apiDbEntryId)
                        break
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // We can perform exponential backoff here
                        // This can be configured by separate apps, but I am putting it here
                        Thread.sleep(backOffTime)
                        backOffTime *= 2
                        retries++
                    }
                }
            }
        }
    }

    companion object {
        val MAX_RETRIES = 5
        val BASE_PATH: String = "./api/response"
    }
}