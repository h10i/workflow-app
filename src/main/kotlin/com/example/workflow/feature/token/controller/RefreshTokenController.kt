package com.example.workflow.feature.token.controller

import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.presenter.RefreshTokenPresenter
import com.example.workflow.feature.token.usecase.RefreshTokenUseCase
import com.example.workflow.feature.token.usecase.RevokeAllRefreshTokensUseCase
import com.example.workflow.feature.token.usecase.RevokeRefreshTokenUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/auth")
class RefreshTokenController(
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val refreshTokenPresenter: RefreshTokenPresenter,
    private val revokeRefreshTokenUseCase: RevokeRefreshTokenUseCase,
    private val revokeAllRefreshTokensUseCase: RevokeAllRefreshTokensUseCase,
) {
    @Operation(
        summary = "Issue a new access token",
        description = "Issues a new access token using a valid refresh token. The refresh token is provided as an HTTP cookie.",
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
        val useCaseResult = refreshTokenUseCase.execute(refreshTokenValue)
        val presenterResult = refreshTokenPresenter.toResponse(useCaseResult)
        return ResponseEntity<TokenResponse>.ok().body(presenterResult.response)
    }

    @Operation(
        summary = "Revoke a specific refresh token",
        description = "Revokes the specified refresh token for the current user. A valid JWT token is required in the Authorization header.",
        security = [SecurityRequirement(name = "bearer-key")],
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "Refresh token successfully revoked (no content)",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authentication credentials are missing or invalid",
                content = [Content()]
            ),
        ],
    )
    @DeleteMapping("/revoke")
    fun revokeRefreshToken(@CookieValue("refreshToken") refreshTokenValue: String): ResponseEntity<Void> {
        revokeRefreshTokenUseCase.execute(refreshTokenValue)
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
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authentication credentials are missing or invalid",
                content = [Content()]
            ),
        ],
    )
    @DeleteMapping("/revoke/all")
    fun revokeAllRefreshTokens(): ResponseEntity<Void> {
        revokeAllRefreshTokensUseCase.execute()
        return ResponseEntity.noContent().build()
    }
}