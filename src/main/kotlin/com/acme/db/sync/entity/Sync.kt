package com.acme.db.sync.entity

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * A class that represents a sync process where we're fetching data from external system. This class
 * will hold state of the api call, transformation and loading to the database.
 */
@Entity
@Table(name = "sync")
data class Sync (
    @Id
    val id: UUID,

    @Column(name = "org_id")
    val orgId: UUID,

    @Column(name = "app_id")
    val appId: UUID,

    @Column(name = "start_time")
    val startTime: LocalDateTime,

    @Column(name = "end_time")
    val endTime: LocalDateTime,

    @Column(name = "status")
    val status: Int,

    // This is really for the people
    @Column(name = "total_records")
    val totalRecords: Int
)