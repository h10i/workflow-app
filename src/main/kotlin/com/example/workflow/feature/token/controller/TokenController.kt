package com.example.workflow.feature.token.controller

import com.example.workflow.core.token.RefreshToken
import com.example.workflow.feature.token.model.TokenRequest
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.service.AuthenticationService
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.feature.token.service.TokenService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/v1/auth")
class TokenController(
    private val authenticationService: AuthenticationService,
    private val tokenService: TokenService,
    private val refreshTokenService: RefreshTokenService,
) {
    @Operation(
        summary = "Authenticate user and issue tokens",
        description = "Authenticates the user with the provided email address and password, then issues a new access token and a refresh token. The refresh token is set as an HTTP-only cookie.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User authentication credentials",
            required = true,
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = TokenRequest::class)
                )
            ]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Authentication successful, access token issued, and refresh token set as cookie",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = TokenResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authentication failed.",
                content = [Content()]
            )
        ],
    )
    @PostMapping("/token")
    fun token(@Valid @RequestBody request: TokenRequest, response: HttpServletResponse): ResponseEntity<TokenResponse> {
        val authentication: Authentication = authenticationService.authenticate(
            request.emailAddress, request.password
        )

        val token = tokenService.generateToken(
            userId = authentication.name,
            scope = authentication.authorities.map { it.authority.toString() }
        )

        val refreshToken: RefreshToken = refreshTokenService.createRefreshToken(UUID.fromString(authentication.name))
        val responseCookie = ResponseCookie
            .from("refreshToken", refreshToken.value)
            .httpOnly(true)
            .secure(true)
            .path("/v1/auth/refresh-token")
            .domain("localhost")
            .maxAge(30 * 24 * 60 * 60)
            .sameSite("None")
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString())

        val tokenResponse = TokenResponse(token)
        return ResponseEntity.ok().body(tokenResponse)
    }
}