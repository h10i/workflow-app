package com.example.workflow.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "role")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = true, unique = true)
    val name: String,

    @OneToMany(mappedBy = "role", cascade = [CascadeType.ALL], orphanRemoval = true)
    val accountRoles: MutableList<AccountRole> = mutableListOf()
)
