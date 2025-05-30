package com.example.workflow.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AuthUser(
    private val account: Account
) : UserDetails {
    override fun getUsername(): String = account.mailAddress
    override fun getPassword(): String = account.password
    override fun getAuthorities(): Collection<GrantedAuthority> = account.roles
}