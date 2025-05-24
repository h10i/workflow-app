package com.example.workflow.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@Entity
@Table(name = "auth_user")
class AuthUser(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "mail_address", nullable = false, unique = true)
    private val mailAddress: String,

    @Column(name = "password", nullable = false)
    private val password: String
) : UserDetails {
    override fun getUsername(): String = mailAddress
    override fun getPassword(): String = password
    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(GrantedAuthority { "ROLE_USER" })
}