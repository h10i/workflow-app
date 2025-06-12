package com.example.workflow.feature.token.controller

import com.example.workflow.core.token.RefreshToken
import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.feature.token.service.TokenService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
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
    @Operation(
        summary = "Issue a new access token",
        description = "Issues a new access token using a valid refresh token. The refresh token is provided as an HTTP cookie.",
        // The refresh token endpoint is typically secured by the refresh token itself, not by a JWT, so no security requirement is added here.
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Access token successfully issued",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = TokenResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Refresh token is invalid or expired",
                content = [Content()]
            )
        ],
    )
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

    @Operation(
        summary = "Revoke a specific refresh token",
        description = "Revokes the specified refresh token for the current user. A valid JWT token is required in the Authorization header.",
        security = [SecurityRequirement(name = "bearer-key")],
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "Refresh token successfully revoked (no content)",
                content = [Content()] // For 204 No Content, content is empty
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authentication credentials are missing or invalid",
                content = [Content()]
            ),
            // Add 404 or other responses as needed
        ],
    )
    @DeleteMapping("/revoke")
    fun revokeRefreshToken(@CookieValue("refreshToken") refreshTokenValue: String): ResponseEntity<Void> {
        val accountId: UUID = accountService.getCurrentAccountId()

        refreshTokenService.revokeRefreshToken(accountId, refreshTokenValue)

        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Revoke all refresh tokens",
        description = "Revokes all refresh tokens for the current user. A valid JWT token is required in the Authorization header.",
        security = [SecurityRequirement(name = "bearer-key")],
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "All refresh tokens successfully revoked (no content)",
                content = [Content()] // For 204 No Content, content is empty
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authentication credentials are missing or invalid",
                content = [Content()]
            ),
        ],
    )
    @DeleteMapping("/revoke/all")
    fun revokeAllRefreshToken(): ResponseEntity<Void> {
        val accountId: UUID = accountService.getCurrentAccountId()

        refreshTokenService.revokeAllRefreshToken(accountId)

        return ResponseEntity.noContent().build()
    }
}