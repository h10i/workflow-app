package com.example.workflow.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import java.util.*

@Entity
@Table(name = "user_role")
data class UserRole(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    val userRoleId: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "auth_user_id")
    val authUser: AuthUser,

    @ManyToOne
    @JoinColumn(name = "role_id")
    val role: Role
) : GrantedAuthority {
    override fun getAuthority(): String? {
        return role.name
    }
}
