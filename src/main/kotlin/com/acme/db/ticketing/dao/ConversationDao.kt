package com.acme.db.ticketing.dao

import com.acme.db.ticketing.entity.Conversation
import com.acme.db.ticketing.entity.Ticket
import org.hibernate.Session
import java.util.UUID

object ConversationDao {
    fun upsert(session: Session, vendorId: String, conversation: Conversation): UUID {
        val existingConversation = session.createQuery("from Conversation where vendorId = :vendorId")
            .setParameter("vendorId", vendorId).setMaxResults(1).uniqueResult() as? Conversation

        if (existingConversation != null) {
            session.update(conversation.copy(id = existingConversation.id))
            return existingConversation.id
        }
        session.save(conversation)
        return conversation.id
    }
}