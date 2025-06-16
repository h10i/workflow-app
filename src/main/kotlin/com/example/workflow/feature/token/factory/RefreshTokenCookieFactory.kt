package com.example.workflow.feature.token.factory

import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class RefreshTokenCookieFactory {
    fun create(refreshTokenValue: String): ResponseCookie {
        return ResponseCookie
            .from("refreshToken", refreshTokenValue)
            .httpOnly(true)
            .secure(true)
            .path("/v1/auth/refresh-token")
            .domain("localhost")
            .maxAge(30 * 24 * 60 * 60)
            .sameSite("None")
            .build()
    }
}