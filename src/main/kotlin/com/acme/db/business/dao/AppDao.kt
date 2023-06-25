package com.acme.db.business.dao

import com.acme.db.business.entity.App
import com.acme.db.util.withSession
import org.hibernate.Session
import java.util.UUID

object AppDao {
    fun getApp(id: UUID): App? {
        return withSession { session ->
            session.get(App::class.java, id)
        }
    }

    fun getByName(name: String): App? {
        return withSession { session ->
            session.createQuery("FROM App where name = :name", App::class.java)
                .setParameter("name", name).singleResult
        }
    }

    fun getInstallationListByOrgId(orgId: UUID, appId: UUID): List<App> {
        return withSession { session ->
            session.createQuery(
                "SELECT oa.app FROM OrgApp oa WHERE oa.org.id = :orgId AND oa.app.id = :appId",
                App::class.java
            ).setParameter("orgId", orgId).setParameter("appId", appId).list()
        }
    }
}
