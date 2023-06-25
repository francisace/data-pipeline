package com.acme.db.sync.entity

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * A class that represents data load to the database after API call was successfully executed. This class
 * will utilize a serialized raw data for transformation.
 */
@Entity
@Table(name = "data_load")
data class DataLoad (
    @Id
    val id: UUID,

    @Column(name = "sync_id", nullable = false)
    val syncId: UUID,

    @Column(name = "api_call_id", nullable = false)
    val apiCallId: UUID,

    @Column(name = "start_time")
    val startTime: LocalDateTime,

    @Column(name = "end_time")
    val endTime: LocalDateTime? = null,

    @Column(name = "status")
    val status: Int = 0,
)