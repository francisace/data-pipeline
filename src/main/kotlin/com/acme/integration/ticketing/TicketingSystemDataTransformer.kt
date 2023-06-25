package com.acme.integration.ticketing

import com.acme.db.ticketing.entity.Ticket

interface TicketingSystemDataTransformer {
    fun getTicketList(filePath: String) : Iterable<Ticket>
}