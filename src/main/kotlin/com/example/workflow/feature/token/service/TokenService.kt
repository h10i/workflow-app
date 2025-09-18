package com.example.workflow.feature.token.service

import com.example.workflow.feature.token.constants.AuthConstants
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class TokenService(private val jwtEncoder: JwtEncoder) {
    fun generateToken(clock: Clock = Clock.systemDefaultZone(), userId: String, scope: List<String>): String {
        val now = Instant.now(clock)
        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(AuthConstants.ACCESS_TOKEN_LIFETIME_MINUTES, ChronoUnit.MINUTES))
            .subject(userId)
            .claim("scope", scope.joinToString(separator = " "))
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}
