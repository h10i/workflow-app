package com.example.workflow.controller

import com.example.workflow.service.TokenService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TokenController(
    private val tokenService: TokenService,
) {
    @GetMapping("/token")
    fun token(): Map<String, String> {

        val token = tokenService.generateToken(
            userId = "test@example.com",
            scope = listOf("message:read")
        )
        return mapOf("token" to token)
    }
}