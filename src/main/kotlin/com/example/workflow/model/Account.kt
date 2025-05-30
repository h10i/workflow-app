package com.example.workflow.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "account")
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "mail_address", nullable = false, unique = true)
    val mailAddress: String,

    @Column(name = "password", nullable = false)
    val password: String,

    @OneToMany(mappedBy = "account", cascade = [CascadeType.ALL], orphanRemoval = true)
    val roles: MutableList<AccountRole> = mutableListOf(),
)
