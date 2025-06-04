package com.example.workflow.feature.token.controller

import com.example.workflow.core.token.RefreshToken
import com.example.workflow.feature.token.model.TokenRequest
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.service.AuthenticationService
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.feature.token.service.TokenService
import jakarta.servlet.http.HttpServletResponse
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
    @PostMapping("/token")
    fun token(@RequestBody request: TokenRequest, response: HttpServletResponse): ResponseEntity<TokenResponse> {
        val authentication: Authentication = authenticationService.authenticate(
            request.mailAddress, request.password
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