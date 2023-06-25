package com.acme.integration.ticketing

import com.acme.db.DbWriter
import com.acme.db.ticketing.dao.ConversationDao
import com.acme.db.ticketing.dao.TicketDao
import com.acme.db.ticketing.entity.Ticket
import com.acme.db.util.withSession

class TicketingSystemDbWriter : DbWriter<Ticket> {
    override fun write(ticketList: Iterable<Ticket>): Int {
        return withSession { session ->
            var count = 0
            for (ticket in ticketList) {
                // check if the external id already exists! this makes sure that we don't re-save
                // the same ticket over and over.
                val ticketUUID = if (ticket.vendorId != null) {
                    TicketDao.upsert(session, ticket.vendorId, ticket)
                } else {
                    session.save(ticket)
                    ticket.id
                }

                if (ticket.conversationList != null) {
                    for (conversation in ticket.conversationList) {
                        val updatedConversation = conversation.copy(ticketId = ticketUUID)
                        if (conversation.vendorId != null) {
                            // Again this is idempotent as it'll check on existing vendor id
                            ConversationDao.upsert(session, conversation.vendorId, updatedConversation)
                        } else {
                            // Otherwise, this just needs to be saved as is...
                            session.save(updatedConversation)
                        }
                    }
                }

                count++
            }

            count
        }
    }
}