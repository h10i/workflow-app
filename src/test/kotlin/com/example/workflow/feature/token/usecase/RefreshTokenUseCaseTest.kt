package com.example.workflow.feature.token.usecase

import com.example.workflow.common.exception.UnauthorizedException
import com.example.workflow.core.account.Account
import com.example.workflow.core.token.RefreshToken
import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.feature.token.service.TokenService
import com.example.workflow.test.util.TestDataFactory
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

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

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ExecuteMethod {
        @Test
        fun `refresh succeeds when valid token is provided`() {
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
        fun `throws Unauthorized Exception when refresh token not found`() {
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
        fun `throws Unauthorized Exception when refresh token is expired`() {
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