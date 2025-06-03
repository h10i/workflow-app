package com.example.workflow.core.role

import com.example.workflow.core.account.AccountRole
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "role")
data class Role(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = true, unique = true)
    val name: String,

    @OneToMany(mappedBy = "role", cascade = [CascadeType.ALL], orphanRemoval = true)
    val accountRoles: MutableList<AccountRole> = mutableListOf()
)