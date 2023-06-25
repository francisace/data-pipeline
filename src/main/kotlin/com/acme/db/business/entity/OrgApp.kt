package com.acme.db.business.entity

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * This is app installation for the given org.
 */
@Entity
@Table(name = "org_app")
data class OrgApp (
    // I am putting this here because there might be multiple installation for any given org
    @Id
    val id: UUID,

    @Column(name = "org_id")
    val orgId: UUID,

    @Column(name = "app_id")
    val appId: UUID,

    @Column(name = "installation_date_time")
    val installationDateTime: LocalDateTime
)