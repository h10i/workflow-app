package com.example.workflow.unit.feature.token.controller

import com.example.workflow.common.exception.UnauthorizedException
import com.example.workflow.feature.token.controller.RefreshTokenController
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.presenter.RefreshTokenPresenter
import com.example.workflow.feature.token.usecase.RefreshTokenUseCase
import com.example.workflow.feature.token.usecase.RevokeAllRefreshTokensUseCase
import com.example.workflow.feature.token.usecase.RevokeRefreshTokenUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.*
import org.junit.jupiter.api.*
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@UnitTest
class RefreshTokenControllerTest {
    private lateinit var refreshTokenUseCase: RefreshTokenUseCase
    private lateinit var refreshTokenPresenter: RefreshTokenPresenter
    private lateinit var revokeRefreshTokenUseCase: RevokeRefreshTokenUseCase
    private lateinit var revokeAllRefreshTokensUseCase: RevokeAllRefreshTokensUseCase
    private lateinit var refreshTokenController: RefreshTokenController

    @BeforeEach
    fun setUp() {
        refreshTokenUseCase = mockk()
        refreshTokenPresenter = mockk()
        revokeRefreshTokenUseCase = mockk()
        revokeAllRefreshTokensUseCase = mockk()
        refreshTokenController = RefreshTokenController(
            refreshTokenUseCase = refreshTokenUseCase,
            refreshTokenPresenter = refreshTokenPresenter,
            revokeRefreshTokenUseCase = revokeRefreshTokenUseCase,
            revokeAllRefreshTokensUseCase = revokeAllRefreshTokensUseCase,
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

            every { revokeRefreshTokenUseCase.execute(tokenValue) } just Runs

            // Act
            val actual = refreshTokenController.revokeRefreshToken(tokenValue)

            // Assert
            verify { revokeRefreshTokenUseCase.execute(tokenValue) }
            assertEquals(HttpStatus.NO_CONTENT, actual.statusCode)
        }
    }

    @Nested
    inner class RevokeAllRefreshTokens {
        @Test
        fun `revoke all refresh tokens succeeds`() {
            // Arrange
            every { revokeAllRefreshTokensUseCase.execute() } just Runs

            // Act
            val actual = refreshTokenController.revokeAllRefreshTokens()

            // Assert
            verify { revokeAllRefreshTokensUseCase.execute() }
            assertEquals(HttpStatus.NO_CONTENT, actual.statusCode)
        }
    }
}