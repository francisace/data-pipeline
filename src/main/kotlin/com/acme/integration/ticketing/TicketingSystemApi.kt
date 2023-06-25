package com.acme.integration.ticketing

import java.io.InputStream
import java.io.OutputStreamWriter
import javax.ws.rs.core.Response

interface TicketingSystemApi {
    fun callApi() : Response
}