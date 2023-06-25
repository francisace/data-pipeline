package com.acme.db.sync.entity

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * A class that represents a sync process where we're fetching data from external system. This class holds
 * the state of the api call
 */
@Entity
@Table(name = "sync")
data class ApiCall (
    @Id
    val id: UUID,

    @Column(name = "sync_id", nullable = false)
    val syncId: UUID,

    @Column(name = "start_time")
    val startTime: LocalDateTime,

    @Column(name = "end_time")
    val endTime: LocalDateTime? = null,

    @Column(name = "status")
    val status: Int = 0,

    @Column(name = "file_location")
    val fileLocation: String? = null
)