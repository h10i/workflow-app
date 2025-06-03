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
    override fun getAuthority(): String? {
        return role.name
    }
}