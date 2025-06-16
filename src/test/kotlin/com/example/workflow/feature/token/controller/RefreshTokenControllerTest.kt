package com.example.workflow.feature.token.controller

import com.example.workflow.core.account.Account
import com.example.workflow.core.token.RefreshToken
import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.feature.token.service.TokenService
import com.example.workflow.testutil.TestDataFactory
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.test.assertEquals

class RefreshTokenControllerTest {
    private lateinit var accountServiceMock: AccountService
    private lateinit var tokenServiceMock: TokenService
    private lateinit var refreshTokenServiceMock: RefreshTokenService
    private lateinit var refreshTokenController: RefreshTokenController

    @BeforeEach
    fun setUp() {
        accountServiceMock = mockk()
        tokenServiceMock = mockk()
        refreshTokenServiceMock = mockk()
        refreshTokenController = RefreshTokenController(
            accountService = accountServiceMock,
            tokenService = tokenServiceMock,
            refreshTokenService = refreshTokenServiceMock
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class RefreshTokenMethod {
        @Test
        fun `refresh succeeds when valid token is provided`() {
            // Arrange
            val tokenValue = "valid-refresh-token"
            val account: Account = TestDataFactory.createAccount()
            val refreshTokenMock: RefreshToken = mockk()
            val expectedAccessTokenValue = "new-access-token-value"

            every { refreshTokenServiceMock.getRefreshTokenByValue(tokenValue) } returns refreshTokenMock
            every { refreshTokenServiceMock.verifyExpiration(refreshTokenMock) } returns refreshTokenMock
            every { refreshTokenMock.account } returns account
            every {
                tokenServiceMock.generateToken(
                    userId = account.id.toString(),
                    scope = account.roles.map { it.role.name }
                )
            } returns expectedAccessTokenValue

            // Act
            val actual = refreshTokenController.refreshToken(tokenValue)

            // Assert
            assertEquals(HttpStatus.OK, actual.statusCode)
            assertEquals(expectedAccessTokenValue, actual.body?.accessToken)
        }

        @Test
        fun `returns 401 when refresh token not found`() {
            // Arrange
            val tokenValue = "missing-token"

            every { refreshTokenServiceMock.getRefreshTokenByValue(tokenValue) } returns null

            // Act
            val actual = refreshTokenController.refreshToken(tokenValue)

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, actual.statusCode)
        }

        @Test
        fun `returns 401 when refresh token is expired`() {
            // Arrange
            val tokenValue = "expired-token"
            val expiredToken: RefreshToken = mockk()

            every { refreshTokenServiceMock.getRefreshTokenByValue(tokenValue) } returns expiredToken
            every { refreshTokenServiceMock.verifyExpiration(expiredToken) } returns null

            // Act
            val actual = refreshTokenController.refreshToken(tokenValue)

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, actual.statusCode)
        }
    }

    @Nested
    inner class RevokeRefreshToken {
        @Test
        fun `revoke refresh token succeeds`() {
            // Arrange
            val tokenValue = "revoked-token"
            val accountId = UUID.randomUUID()

            every { accountServiceMock.getCurrentAccountId() } returns accountId
            every { refreshTokenServiceMock.revokeRefreshToken(accountId, tokenValue) } returns 1

            // Act
            val actual = refreshTokenController.revokeRefreshToken(tokenValue)

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, actual.statusCode)
        }
    }

    @Nested
    inner class RevokeAllRefreshTokens {
        @Test
        fun `revoke all refresh tokens succeeds`() {
            // Arrange
            val accountId = UUID.randomUUID()

            every { accountServiceMock.getCurrentAccountId() } returns accountId
            every { refreshTokenServiceMock.revokeAllRefreshTokens(accountId) } returns 2

            // Act
            val actual = refreshTokenController.revokeAllRefreshTokens()

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, actual.statusCode)
        }
    }
}