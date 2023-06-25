package com.acme.db.util
// File name: HibernateUtil.kt
import com.acme.db.business.entity.App
import com.acme.db.business.entity.Org
import com.acme.db.business.entity.OrgApp
import com.acme.db.sync.entity.ApiCall
import com.acme.db.sync.entity.DataLoad
import com.acme.db.sync.entity.IntegrationSync
import com.acme.db.ticketing.entity.Conversation
import com.acme.db.ticketing.entity.Ticket
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

private val sessionFactory: SessionFactory by lazy {
    val configuration = Configuration()
    configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver")
    configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
    configuration.setProperty("hibernate.connection.username", "sa")
    configuration.setProperty("hibernate.connection.password", "")
    configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
    configuration.setProperty("hibernate.hbm2ddl.auto", "create")

    configuration.addAnnotatedClass(App::class.java)
    configuration.addAnnotatedClass(Org::class.java)
    configuration.addAnnotatedClass(OrgApp::class.java)

    configuration.addAnnotatedClass(ApiCall::class.java)
    configuration.addAnnotatedClass(DataLoad::class.java)
    configuration.addAnnotatedClass(IntegrationSync::class.java)

    configuration.addAnnotatedClass(Conversation::class.java)
    configuration.addAnnotatedClass(Ticket::class.java)

    configuration.buildSessionFactory()
}

fun <T> withSession(action: (Session) -> T): T {
    val session: Session = sessionFactory.openSession()
    val transaction: Transaction = session.beginTransaction()
    return try {
        val result = action(session)
        transaction.commit()
        result
    } catch (e: Exception) {
        transaction.rollback()
        throw e
    } finally {
        session.close()
    }
}
