import com.acme.db.business.dao.AppDao
import com.acme.db.business.entity.App
import com.acme.db.business.entity.Org
import com.acme.db.business.entity.OrgApp
import com.acme.db.util.withSession
import java.time.LocalDateTime
import java.util.UUID

fun initDb() {
    // Create org for Acme
    val org = Org(
        id = UUID.randomUUID(),
        name = "acme",
        url = "http://www.acme.com"
    )

    // Create the app for Intercom
    val app = App(
        id = UUID.randomUUID(),
        name = "intercom"
    )

    val orgApp = OrgApp(
        id = UUID.randomUUID(),
        orgId = org.id,
        appId = app.id,
        installationDateTime = LocalDateTime.now()
    )

    withSession { session ->
        session.save(org)
        session.save(app)
        session.save(orgApp)
    }

    println(AppDao.getApp(app.id))
    println(AppDao.getInstallationListByOrgId(org.id, app.id))
}

fun initKafka() {
}

fun main(args: Array<String>) {
    initDb()
}