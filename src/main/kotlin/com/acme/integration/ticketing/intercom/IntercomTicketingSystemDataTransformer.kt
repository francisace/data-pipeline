package com.acme.integration.ticketing.intercom

import com.acme.db.ticketing.entity.Conversation
import com.acme.db.ticketing.entity.Ticket
import com.acme.integration.ticketing.TicketingSystemDataTransformer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.time.LocalDateTime
import java.util.*

class IntercomTicketingSystemDataTransformer : TicketingSystemDataTransformer {

    // Note, this takes in filePath, but it should really take in inputstream to make it even more generic
    override fun getTicketList(filePath: String): Iterable<Ticket> {
        // Map the JSON string to a JsonData object.
        val jsonData = MAPPER.readValue(File(filePath), TicketData::class.java)

        // Map the JsonData object to a Ticket entity and return it in a list.
        return listOf(
            Ticket(
                id = UUID.randomUUID(),
                vendorId = jsonData.id,
                title = jsonData.title,
                createAt = LocalDateTime.now(),
                conversationList = jsonData.conversationParts.conversationParts.map { jsonConversation ->
                    Conversation(
                        id = UUID.randomUUID(),
                        ticketId = UUID.randomUUID(),  // Replace with actual ticket ID.
                        vendorId = jsonConversation.id,
                        body = jsonConversation.body,
                        createAt = LocalDateTime.now()  // Replace with actual creation time.
                    )
                }
            )
        )
    }

    companion object {
        val MAPPER = jacksonObjectMapper()
    }
}

data class TicketData(
    val id: String,
    val title: String,
    @JsonProperty("conversation_parts") val conversationParts: ConversationPartsData
)

data class ConversationPartsData(
    @JsonProperty("conversation_parts") val conversationParts: List<ConversationData>
)

data class ConversationData(
    val body: String,
    val id: String
)