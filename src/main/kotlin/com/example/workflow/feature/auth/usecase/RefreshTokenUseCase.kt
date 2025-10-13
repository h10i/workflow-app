package com.example.workflow.feature.auth.usecase

import com.example.workflow.common.exception.UnauthorizedException
import com.example.workflow.core.auth.RefreshToken
import com.example.workflow.feature.auth.service.RefreshTokenService
import com.example.workflow.feature.auth.service.TokenService
import org.springframework.stereotype.Service

@Service
class RefreshTokenUseCase(
    private val tokenService: TokenService,
    private val refreshTokenService: RefreshTokenService,
) {
    data class Result(
        val refreshToken: RefreshToken,
        val accessToken: String,
    )

    fun execute(refreshTokenValue: String): Result {
        val refreshToken: RefreshToken = refreshTokenService.getRefreshTokenByValue(refreshTokenValue)
            ?: throw UnauthorizedException()

        val verifiedRefreshToken: RefreshToken = refreshTokenService.verifyExpiration(refreshToken)
            ?: throw UnauthorizedException()

        val accessToken: String = tokenService.generateToken(
            userId = verifiedRefreshToken.account.id.toString(),
            scope = verifiedRefreshToken.account.roles.map { it.role.name },
        )

        return Result(
            refreshToken = verifiedRefreshToken,
            accessToken = accessToken,
        )
    }
}
