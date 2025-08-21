package com.example.workflow.core.account

import com.example.workflow.core.role.Role
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import java.util.*

@Entity
@Table(name = "account_role")
data class AccountRole(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "account_id")
    val account: Account,

    @ManyToOne
    @JoinColumn(name = "role_id")
    val role: Role
) : GrantedAuthority {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountRole

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "AccountRole(id=$id, account=${account.id}, role=${role.id})"
    }

    override fun getAuthority(): String {
        return role.name
    }
}