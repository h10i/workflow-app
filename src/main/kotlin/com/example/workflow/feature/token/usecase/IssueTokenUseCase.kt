package com.example.workflow.feature.token.usecase

import com.example.workflow.core.token.RefreshToken
import com.example.workflow.feature.token.factory.RefreshTokenCookieFactory
import com.example.workflow.feature.token.model.TokenRequest
import com.example.workflow.feature.token.service.AuthenticationService
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.feature.token.service.TokenService
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
            request.emailAddress, request.password
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