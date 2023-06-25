package com.acme.db.sync.dao

import com.acme.db.sync.entity.ApiCall
import com.acme.db.util.withSession
import java.util.UUID

object ApiCallDao {
    fun find(id: UUID): ApiCall? {
        return withSession { session ->
            session.find(ApiCall::class.java, id)
        }
    }

    fun updateComplete(apiCallId: UUID, fileLocation: String) : Int {
        return withSession { session ->
            session.createQuery("UPDATE ApiCall SET fileLocation = :fileLocation, status = 1 WHERE id = :id")
                .setParameter("fileLocation", fileLocation)
                .setParameter("id", apiCallId)
                .executeUpdate()
        }
    }
}