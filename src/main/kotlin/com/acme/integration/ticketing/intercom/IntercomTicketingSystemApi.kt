package com.acme.integration.ticketing.intercom

import com.acme.integration.ticketing.TicketingSystemApi
import java.net.URI
import java.util.*
import javax.ws.rs.core.*

class IntercomTicketingSystemApi : TicketingSystemApi {
    override fun callApi(): Response {
        // In reality this would make a http request and return the actual HTTP response
        // the underlying entity would be InputStream
        return FakeResponse()
    }
}

class FakeResponse : Response() {
    override fun close() {
    }

    override fun getStatus(): Int {
        return 200
    }

    override fun getStatusInfo(): StatusType {
        TODO("Not yet implemented")
    }

    override fun getEntity(): Any {
        return this::class.java.classLoader.getResourceAsStream("mock/intercom/data.json")
    }

    override fun <T : Any?> readEntity(entityType: Class<T>?): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> readEntity(entityType: GenericType<T>?): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> readEntity(entityType: Class<T>?, annotations: Array<out Annotation>?): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> readEntity(entityType: GenericType<T>?, annotations: Array<out Annotation>?): T {
        TODO("Not yet implemented")
    }

    override fun hasEntity(): Boolean {
        TODO("Not yet implemented")
    }

    override fun bufferEntity(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMediaType(): MediaType {
        TODO("Not yet implemented")
    }

    override fun getLanguage(): Locale {
        TODO("Not yet implemented")
    }

    override fun getLength(): Int {
        TODO("Not yet implemented")
    }

    override fun getAllowedMethods(): MutableSet<String> {
        TODO("Not yet implemented")
    }

    override fun getCookies(): MutableMap<String, NewCookie> {
        TODO("Not yet implemented")
    }

    override fun getEntityTag(): EntityTag {
        TODO("Not yet implemented")
    }

    override fun getDate(): Date {
        TODO("Not yet implemented")
    }

    override fun getLastModified(): Date {
        TODO("Not yet implemented")
    }

    override fun getLocation(): URI {
        TODO("Not yet implemented")
    }

    override fun getLinks(): MutableSet<Link> {
        TODO("Not yet implemented")
    }

    override fun hasLink(relation: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLink(relation: String?): Link {
        TODO("Not yet implemented")
    }

    override fun getLinkBuilder(relation: String?): Link.Builder {
        TODO("Not yet implemented")
    }

    override fun getMetadata(): MultivaluedMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun getStringHeaders(): MultivaluedMap<String, String> {
        TODO("Not yet implemented")
    }

    override fun getHeaderString(name: String?): String {
        TODO("Not yet implemented")
    }

}