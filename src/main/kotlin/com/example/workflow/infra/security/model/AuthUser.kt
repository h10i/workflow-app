package com.example.workflow.infra.security.model

import com.example.workflow.core.account.Account
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AuthUser(
    private val account: Account
) : UserDetails {
    override fun getUsername(): String = account.id.toString()
    override fun getPassword(): String = account.password
    override fun getAuthorities(): Collection<GrantedAuthority> = account.roles
}