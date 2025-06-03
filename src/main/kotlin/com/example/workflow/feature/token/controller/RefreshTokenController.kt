package com.example.workflow.feature.token.controller

import com.example.workflow.core.token.RefreshToken
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.feature.token.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RefreshTokenController(
    private val tokenService: TokenService,
    private val refreshTokenService: RefreshTokenService,
) {
    @PostMapping("/refresh-token")
    fun refreshToken(@CookieValue("refreshToken") refreshTokenValue: String): ResponseEntity<TokenResponse> {
        val refreshToken: RefreshToken = refreshTokenService.getRefreshTokenByValue(refreshTokenValue)
            ?: return ResponseEntity<TokenResponse>.status(HttpStatus.UNAUTHORIZED).build()

        val verifiedRefreshToken: RefreshToken = refreshTokenService.verifyExpiration(refreshToken)
            ?: return ResponseEntity<TokenResponse>.status(HttpStatus.UNAUTHORIZED).build()

        val accessToken: String = tokenService.generateToken(
            userId = verifiedRefreshToken.account.id.toString(),
            scope = verifiedRefreshToken.account.roles.map { it.role.name },
        )

        return ResponseEntity<TokenResponse>.ok().body(TokenResponse(accessToken))
    }
}