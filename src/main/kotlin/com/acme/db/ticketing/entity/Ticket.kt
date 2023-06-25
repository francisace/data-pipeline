package com.acme.db.ticketing.entity

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.Transient

/**
 * A class that represents a sync process where we're fetching data from external system. This class holds
 * the state of the api call
 */
@Entity
@Table(name = "ticket")
data class Ticket (
    @Id
    val id: UUID,

    @Column(name = "vendor_id")
    val vendorId: String?,

    @Column(name = "title")
    val title: String?,

    @Column(name = "create_at")
    val createAt: LocalDateTime?,

    @Transient
    val conversationList: List<Conversation> = emptyList()
)