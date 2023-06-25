package com.acme.db.ticketing.entity

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Nested conversation within the ticket
 */
@Entity
@Table(name = "conversation")
data class Conversation (
    @Id
    val id: UUID,

    @Column(name = "ticket_id", nullable = false)
    val ticketId: UUID,

    @Column(name = "vendor_id")
    val vendorId: String?,

    @Column(name = "body")
    val body: String?,

    @Column(name = "create_at")
    val createAt: LocalDateTime?
)