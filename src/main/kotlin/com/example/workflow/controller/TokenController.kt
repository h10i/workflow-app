package com.example.workflow.controller

import com.example.workflow.model.TokenRequest
import com.example.workflow.model.TokenResponse
import com.example.workflow.service.AuthenticationService
import com.example.workflow.service.TokenService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TokenController(
    private val authenticationService: AuthenticationService,
    private val tokenService: TokenService,
) {
    @PostMapping("/token")
    fun token(@RequestBody request: TokenRequest): ResponseEntity<TokenResponse> {
        val authentication: Authentication = authenticationService.authenticate(
            request.mailAddress, request.password
        )

        val token = tokenService.generateToken(
            userId = authentication.name,
            scope = authentication.authorities.map { it.authority.toString() }
        )

        val tokenResponse = TokenResponse(token)
        return ResponseEntity.ok().body(tokenResponse)
    }
}