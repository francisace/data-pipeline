package com.acme.db.business.entity

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "app")
data class Org (
    @Id
    val id: UUID,

    @Column(name = "name")
    val name: String,

    @Column(name = "url")
    val url: String?
)