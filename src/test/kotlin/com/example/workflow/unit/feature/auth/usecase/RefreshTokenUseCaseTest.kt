package com.example.workflow.unit.feature.auth.usecase

import com.example.workflow.common.exception.UnauthorizedException
import com.example.workflow.core.account.Account
import com.example.workflow.core.auth.RefreshToken
import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.auth.service.RefreshTokenService
import com.example.workflow.feature.auth.service.TokenService
import com.example.workflow.feature.auth.usecase.RefreshTokenUseCase
import com.example.workflow.support.annotation.UnitTest
import com.example.workflow.support.util.TestDataFactory
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@UnitTest
class RefreshTokenUseCaseTest {
    private lateinit var accountServiceMock: AccountService
    private lateinit var tokenServiceMock: TokenService
    private lateinit var refreshTokenServiceMock: RefreshTokenService
    private lateinit var refreshTokenUseCase: RefreshTokenUseCase

    @BeforeEach
    fun setUp() {
        accountServiceMock = mockk()
        tokenServiceMock = mockk()
        refreshTokenServiceMock = mockk()
        refreshTokenUseCase = RefreshTokenUseCase(
            tokenService = tokenServiceMock,
            refreshTokenService = refreshTokenServiceMock
        )
    }

    @Nested
    inner class ExecuteFun {
        @Test
        fun `should refresh token when valid token`() {
            // Arrange
            val refreshTokenValue = "valid-refresh-token"
            val account: Account = TestDataFactory.createAccount()
            val refreshTokenMock: RefreshToken = mockk()
            val expectedAccessTokenValue = "new-access-token-value"

            every { refreshTokenServiceMock.getRefreshTokenByValue(refreshTokenValue) } returns refreshTokenMock
            every { refreshTokenServiceMock.verifyExpiration(refreshTokenMock) } returns refreshTokenMock
            every { refreshTokenMock.account } returns account
            every {
                tokenServiceMock.generateToken(
                    userId = account.id.toString(),
                    scope = account.roles.map { it.role.name }
                )
            } returns expectedAccessTokenValue

            // Act
            val actual = refreshTokenUseCase.execute(refreshTokenValue)

            // Assert
            assertEquals(refreshTokenMock, actual.refreshToken)
            assertEquals(expectedAccessTokenValue, actual.accessToken)
        }

        @Test
        fun `should throw Unauthorized Exception when a refresh token does not exist`() {
            // Arrange
            val tokenValue = "missing-token"

            every { refreshTokenServiceMock.getRefreshTokenByValue(tokenValue) } returns null

            // Act
            // Assert
            assertThrows<UnauthorizedException> {
                refreshTokenUseCase.execute(tokenValue)
            }
        }

        @Test
        fun `should throw Unauthorized Exception when expired refresh token`() {
            // Arrange
            val tokenValue = "expired-token"
            val expiredToken: RefreshToken = mockk()

            every { refreshTokenServiceMock.getRefreshTokenByValue(tokenValue) } returns expiredToken
            every { refreshTokenServiceMock.verifyExpiration(expiredToken) } returns null

            // Act
            // Assert
            assertThrows<UnauthorizedException> {
                refreshTokenUseCase.execute(tokenValue)
            }
        }
    }
}
