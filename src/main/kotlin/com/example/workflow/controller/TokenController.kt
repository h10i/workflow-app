package com.example.workflow.controller

import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
class TokenController(private val jwtEncoder: JwtEncoder) {
    @GetMapping("/token")
    fun token(): Map<String, String> {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(3600))
            .subject("test@example.com")
            .claim("scope", "message:read")
            .build()

        val token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
        return mapOf("token" to token)
    }
}