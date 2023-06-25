package com.acme.integration.ticketing.intercom

import com.acme.db.DbWriter
import com.acme.db.ticketing.entity.Ticket
import com.acme.integration.ticketing.TicketSystemResourceProvider
import com.acme.integration.ticketing.TicketingSystemApi
import com.acme.integration.ticketing.TicketingSystemDataTransformer
import com.acme.integration.ticketing.TicketingSystemDbWriter

class IntercomTicketSystemResourceProvider : TicketSystemResourceProvider<Ticket> {
    override fun getDbWriter(): DbWriter<Ticket> {
        return TicketingSystemDbWriter()
    }

    override fun getApi(): TicketingSystemApi {
        return IntercomTicketingSystemApi()
    }

    override fun getDataTransformer(): TicketingSystemDataTransformer {
        return IntercomTicketingSystemDataTransformer()
    }
}