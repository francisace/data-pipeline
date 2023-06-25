package com.acme.db.business.entity

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * App description. It contains name of the app and how the data should be pulled and transformed.
 */
@Entity
@Table(name = "app")
data class App (
    @Id
    val id: UUID,

    @Column(name = "name")
    val name: String
)