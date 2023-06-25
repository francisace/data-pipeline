package com.acme.db.ticketing.dao

import com.acme.db.ticketing.entity.Ticket
import org.hibernate.Session
import java.util.*

object TicketDao {
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