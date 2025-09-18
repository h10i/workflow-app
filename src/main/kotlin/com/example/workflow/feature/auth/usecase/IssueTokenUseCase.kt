package com.example.workflow.feature.auth.usecase

import com.example.workflow.core.auth.RefreshToken
import com.example.workflow.feature.auth.factory.RefreshTokenCookieFactory
import com.example.workflow.feature.auth.model.TokenRequest
import com.example.workflow.feature.auth.service.AuthenticationService
import com.example.workflow.feature.auth.service.RefreshTokenService
import com.example.workflow.feature.auth.service.TokenService
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*

@Service
class IssueTokenUseCase(
    private val authenticationService: AuthenticationService,
    private val tokenService: TokenService,
    private val refreshTokenService: RefreshTokenService,
    private val refreshTokenCookieFactory: RefreshTokenCookieFactory,
) {
    data class Result(
        val accessToken: String,
        val refreshTokenCookie: ResponseCookie
    )

    fun execute(request: TokenRequest): Result {
        val authentication: Authentication = authenticationService.authenticate(
            request.emailAddress,
            request.password
        )

        val accessToken = tokenService.generateToken(
            userId = authentication.name,
            scope = authentication.authorities.map { it.authority.toString() }
        )

        val refreshToken: RefreshToken = refreshTokenService.createRefreshToken(UUID.fromString(authentication.name))
        val responseCookie = refreshTokenCookieFactory.create(refreshToken.value)

        return Result(accessToken, responseCookie)
    }
}
