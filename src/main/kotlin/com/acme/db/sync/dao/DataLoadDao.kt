package com.acme.db.sync.dao

import org.hibernate.Session
import java.time.LocalDateTime
import java.util.UUID

object DataLoadDao {
    fun updateComplete(session: Session, id: UUID): Int {
        return session.createQuery("UPDATE DataLoad SET status = 1, endTime = :endTime WHERE id = :id")
            .setParameter("endTime", LocalDateTime.now())
            .setParameter("id", id)
            .executeUpdate()
    }
}