package com.acme.kafka.consumer

import com.acme.db.sync.dao.ApiCallDao
import com.acme.db.sync.dao.DataLoadDao
import com.acme.db.sync.dao.SyncDao
import com.acme.db.sync.entity.ApiCall
import com.acme.db.sync.entity.DataLoad
import com.acme.db.util.withSession
import com.acme.integration.ticketing.TicketingSystemDbWriter
import com.acme.integration.ticketing.intercom.IntercomTicketingSystemDataTransformer
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.lang.Exception
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

/**
 * This consumer is responsible for converting downloaded data and then translating it into DB object
 * and serialize the data. It also updates
 */
class IntegrationDbWriterConsumer(val consumer: KafkaConsumer<String, UUID>) : Runnable {
    override fun run() {
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))

            for (record in records) {
                val apiCallId = record.value()
                val apiCall = ApiCallDao.findValidApiCall(apiCallId) ?: throw Exception("This record should exist")
                apiCall.fileLocation ?: throw Exception("File path is missing")

                // I am hard coding this here, this should be dynamically loaded
                val intercomTicketingSystemDataTransformer = IntercomTicketingSystemDataTransformer()
                val ticketList = intercomTicketingSystemDataTransformer.getTicketList(apiCall.fileLocation)

                // Create new entry in the db to record the call into the api
                val dataLoadId = withSession { session ->
                    session.save(
                        DataLoad(
                            id = UUID.randomUUID(),
                            syncId = apiCall.syncId,
                            apiCallId = apiCallId,
                            startTime = LocalDateTime.now()
                        )
                    ) as UUID
                }

                val ticketingSystemDbWriter = TicketingSystemDbWriter()
                val totalCount = ticketingSystemDbWriter.write(ticketList)

                // We want to mark the top level sync to be completed while updating the nested
                withSession { session ->
                    SyncDao.updateComplete(session, apiCall.syncId, totalCount)
                    DataLoadDao.updateComplete(session, dataLoadId)
                }
            }
        }
    }
}