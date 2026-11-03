package com.taskflow.backend.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "projects")
class Project(
    @Column(nullable = false)
    var name: String,

    @Column(length = 1000)
    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    val owner: User,

    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
)