package com.example.workflow.controller

import com.example.workflow.model.TokenRequest
import com.example.workflow.service.TokenService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TokenController(
    private val authenticationManager: AuthenticationManager,
    private val tokenService: TokenService,
) {
    @PostMapping("/token")
    fun token(@RequestBody request: TokenRequest): Map<String, String> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.mailAddress, request.password)
        )

        val token = tokenService.generateToken(
            userId = authentication.name,
            scope = listOf("message:read")
        )
        return mapOf("token" to token)
    }
}