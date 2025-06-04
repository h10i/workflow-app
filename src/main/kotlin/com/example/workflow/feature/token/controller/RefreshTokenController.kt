package com.example.workflow.feature.token.controller

import com.example.workflow.core.token.RefreshToken
import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.feature.token.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/auth")
class RefreshTokenController(
    private val accountService: AccountService,
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

    @DeleteMapping("/revoke")
    fun revokeRefreshToken(@CookieValue("refreshToken") refreshTokenValue: String): ResponseEntity<Void> {
        val accountId: UUID = accountService.getCurrentAccountId()

        refreshTokenService.revokeRefreshToken(accountId, refreshTokenValue)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/revoke/all")
    fun revokeAllRefreshToken(): ResponseEntity<Void> {
        val accountId: UUID = accountService.getCurrentAccountId()

        refreshTokenService.revokeAllRefreshToken(accountId)

        return ResponseEntity.noContent().build()
    }
}