package com.example.workflow.feature.token.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val authenticationManager: AuthenticationManager,
) {
    fun authenticate(emailAddress: String, password: String): Authentication {
        return authenticationManager.authenticate(UsernamePasswordAuthenticationToken(emailAddress, password))
    }
}