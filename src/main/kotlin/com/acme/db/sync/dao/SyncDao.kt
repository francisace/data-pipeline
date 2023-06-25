package com.acme.db.sync.dao

import com.acme.db.sync.entity.ApiCall
import com.acme.db.sync.entity.Sync
import com.acme.db.util.withSession
import org.hibernate.Session
import java.time.LocalDateTime
import java.util.UUID

object SyncDao {
    fun find(syncId: UUID): Sync? {
        return withSession { session ->
            session.find(Sync::class.java, syncId)
        }
    }

    fun updateComplete(session: Session, id: UUID, totalRecordCount: Int): Int {
        return session.createQuery("UPDATE Sync SET status = 1, endTime = :endTime WHERE id = :id")
            .setParameter("totalRecordCount", totalRecordCount)
            .setParameter("endTime", LocalDateTime.now())
            .setParameter("id", id)
            .executeUpdate()
    }
}