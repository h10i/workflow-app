package com.example.workflow.feature.token.controller

import com.example.workflow.common.exception.UnauthorizedException
import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.presenter.RefreshTokenPresenter
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.feature.token.service.TokenService
import com.example.workflow.feature.token.usecase.RefreshTokenUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.*
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.test.assertEquals

class RefreshTokenControllerTest {
    private lateinit var refreshTokenUseCase: RefreshTokenUseCase
    private lateinit var refreshTokenPresenter: RefreshTokenPresenter
    private lateinit var accountServiceMock: AccountService
    private lateinit var tokenServiceMock: TokenService
    private lateinit var refreshTokenServiceMock: RefreshTokenService
    private lateinit var refreshTokenController: RefreshTokenController

    @BeforeEach
    fun setUp() {
        refreshTokenUseCase = mockk()
        refreshTokenPresenter = mockk()
        accountServiceMock = mockk()
        tokenServiceMock = mockk()
        refreshTokenServiceMock = mockk()
        refreshTokenController = RefreshTokenController(
            refreshTokenUseCase = refreshTokenUseCase,
            refreshTokenPresenter = refreshTokenPresenter,
            accountService = accountServiceMock,
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
            val refreshTokenValue = "valid-refresh-token"
            val expectedAccessTokenValue = "new-access-token-value"
            val useCaseResult = RefreshTokenUseCase.Result(
                refreshToken = mockk(),
                accessToken = expectedAccessTokenValue,
            )

            val tokenResponse = TokenResponse(
                accessToken = expectedAccessTokenValue
            )
            val presenterResult = RefreshTokenPresenter.Result(
                response = tokenResponse
            )

            every { refreshTokenUseCase.execute(refreshTokenValue) } returns useCaseResult
            every { refreshTokenPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val actual = refreshTokenController.refreshToken(refreshTokenValue)

            // Assert
            assertEquals(HttpStatus.OK, actual.statusCode)
            assertEquals(expectedAccessTokenValue, actual.body?.accessToken)
        }

        @Test
        fun `throws Unauthorized Exception when invalid credentials`() {
            // Arrange
            val refreshTokenValue = "invalid-token"

            every { refreshTokenUseCase.execute(refreshTokenValue) } throws UnauthorizedException()

            // Act
            // Assert
            assertThrows<UnauthorizedException> {
                refreshTokenController.refreshToken(refreshTokenValue)
            }
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