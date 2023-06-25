package com.acme.db.ticketing.dao

import com.acme.db.ticketing.entity.Conversation
import com.acme.db.ticketing.entity.Ticket
import org.hibernate.Session
import java.util.UUID

object ConversationDao {
    fun upsert(session: Session, vendorId: String, conversation: Conversation): UUID {
        val existingVendorTicket = session.createQuery("from Conversation where vendorId = :vendorId")
            .setParameter("vendorId", vendorId).setMaxResults(1).uniqueResult() as Ticket

        if (existingVendorTicket != null) {
            session.update(conversation.copy(id = existingVendorTicket.id))
            return existingVendorTicket.id
        }
        session.save(conversation)
        return conversation.id
    }
}