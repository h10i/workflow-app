package com.example.workflow.core.account

import com.example.workflow.feature.account.model.AccountViewDto
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

fun Account.toViewDto(): AccountViewDto = AccountViewDto(
    id = id,
    mailAddress = mailAddress,
    roleNames = roles.map { it.role.name }
)
