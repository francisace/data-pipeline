import com.acme.db.business.entity.App
import com.acme.db.business.entity.Org
import java.util.UUID

fun initDb() {
    // Create org for Acme
    val org = Org(
        id = UUID.randomUUID(),
        name = "acme",
        url = "http://www.acme.com"  // Replace with actual URL.
    )

    // Create the app for Intercom
    val app = App(
        id = UUID.randomUUID(),
        name = "intercom"
    )

}

fun initKafka() {
}

fun main(args: Array<String>) {
}