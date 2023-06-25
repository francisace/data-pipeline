package com.acme.db.sync.dao

import com.acme.db.sync.entity.IntegrationSync
import com.acme.db.util.withSession
import org.hibernate.Session
import java.time.LocalDateTime
import java.util.UUID

object IntegrationSyncDao {
    fun find(syncId: UUID): IntegrationSync? {
        return withSession { session ->
            session.find(IntegrationSync::class.java, syncId)
        }
    }

    fun updateComplete(session: Session, id: UUID, totalRecords: Int): Int {
        return session.createQuery("UPDATE IntegrationSync SET status = 1, endTime = :endTime, totalRecords = :totalRecords WHERE id = :id")
            .setParameter("totalRecords", totalRecords)
            .setParameter("endTime", LocalDateTime.now())
            .setParameter("id", id)
            .executeUpdate()
    }
}