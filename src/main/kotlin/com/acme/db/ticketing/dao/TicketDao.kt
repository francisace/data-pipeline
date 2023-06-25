package com.acme.db.ticketing.dao

import com.acme.db.ticketing.entity.Ticket
import com.acme.db.util.withSession
import org.hibernate.Session
import java.util.*

object TicketDao {
    fun findAll() : List<Ticket> {
        return withSession {session ->
            session.createQuery("from Ticket").list() as List<Ticket>
        }
    }

    fun upsert(session: Session, vendorId: String, ticket: Ticket): UUID {
        val existingVendorTicket = session.createQuery("from Ticket where vendorId = :vendorId")
            .setParameter("vendorId", vendorId).setMaxResults(1).uniqueResult() as? Ticket

        if (existingVendorTicket != null) {
            session.update(ticket.copy(id = existingVendorTicket.id))
            return existingVendorTicket.id
        }
        session.save(ticket)
        return ticket.id
    }
}