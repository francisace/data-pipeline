package com.acme.db.sync.dao

import com.acme.db.sync.entity.ApiCall
import com.acme.db.util.withSession
import java.util.UUID

object ApiCallDao {
    fun findValidApiCall(syncId: UUID) : ApiCall? {
        return withSession { session ->
            session.createQuery(
                "from ApiCall where syncId = :syncId and status = 1",
                ApiCall::class.java
            ).setParameter("syncId", syncId)
                .setMaxResults(1)
                .uniqueResult()
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