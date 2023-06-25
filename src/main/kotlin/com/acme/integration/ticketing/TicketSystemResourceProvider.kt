package com.acme.integration.ticketing

import com.acme.db.DbWriter

interface TicketSystemResourceProvider<V> {
    fun getDbWriter(): DbWriter<V>
    fun getApi(): TicketingSystemApi
    fun getDataTransformer(): TicketingSystemDataTransformer
}