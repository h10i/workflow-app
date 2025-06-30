package com.example.workflow.feature.token.controller

import com.example.workflow.common.exception.UnauthorizedException
import com.example.workflow.common.path.ApiPath
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.presenter.RefreshTokenPresenter
import com.example.workflow.feature.token.usecase.RefreshTokenUseCase
import com.example.workflow.feature.token.usecase.RevokeAllRefreshTokensUseCase
import com.example.workflow.feature.token.usecase.RevokeRefreshTokenUseCase
import com.example.workflow.test.config.NoSecurityConfig
import io.mockk.*
import jakarta.servlet.http.Cookie
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.assertj.MvcTestResult
import kotlin.test.assertEquals

@WebMvcTest(RefreshTokenController::class)
@Import(RefreshTokenControllerApiTest.MockConfig::class, NoSecurityConfig::class)
class RefreshTokenControllerApiTest {
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    @Autowired
    private lateinit var refreshTokenUseCase: RefreshTokenUseCase

    @Autowired
    private lateinit var refreshTokenPresenter: RefreshTokenPresenter

    @Autowired
    private lateinit var revokeRefreshTokenUseCase: RevokeRefreshTokenUseCase

    @Autowired
    private lateinit var revokeAllRefreshTokensUseCase: RevokeAllRefreshTokensUseCase

    @TestConfiguration
    class MockConfig {
        @Bean
        fun refreshTokenUseCase(): RefreshTokenUseCase = mockk(relaxed = true)

        @Bean
        fun refreshTokenPresenter(): RefreshTokenPresenter = mockk(relaxed = true)

        @Bean
        fun revokeRefreshTokenUseCase(): RevokeRefreshTokenUseCase = mockk(relaxed = true)

        @Bean
        fun revokeAllRefreshTokensUseCase(): RevokeAllRefreshTokensUseCase = mockk(relaxed = true)
    }

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    inner class RefreshToken {
        @Test
        fun `refreshToken endpoint should return new token on valid refresh token`() {
            // Arrange
            val refreshTokenValue = "valid-refresh-token"
            val useCaseResult: RefreshTokenUseCase.Result = mockk()
            val accessToken = "new-access-token"
            val presenterResult = RefreshTokenPresenter.Result(
                response = TokenResponse(
                    accessToken = accessToken
                )
            )

            every { refreshTokenUseCase.execute(any()) } returns useCaseResult
            every { refreshTokenPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .post()
                .uri("${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REFRESH_TOKEN}")
                .cookie(Cookie("refreshToken", refreshTokenValue))
                .exchange()

            // Assert
            val request = slot<String>()
            verify(exactly = 1) { refreshTokenUseCase.execute(capture(request)) }
            val capturedRequest = request.captured
            assertEquals(refreshTokenValue, capturedRequest)

            assertThat(testResult)
                .hasStatusOk()
                .bodyJson()
                .isLenientlyEqualTo(
                    """
                {
                    "accessToken": "$accessToken"
                }
                """.trimIndent()
                )
        }

        @Test
        fun `refreshToken endpoint should return UNAUTHORIZED when refresh token is invalid`() {
            // Arrange
            val refreshTokenValue = "valid-refresh-token"

            every { refreshTokenUseCase.execute(any()) } throws UnauthorizedException()

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .post()
                .uri("${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REFRESH_TOKEN}")
                .cookie(Cookie("refreshToken", refreshTokenValue))
                .exchange()

            // Assert
            assertThat(testResult)
                .hasStatus(HttpStatus.UNAUTHORIZED)
        }
    }

    @Nested
    inner class RevokeRefreshToken() {
        @Test
        fun `revoke endpoint should revoke refresh token and return no content`() {
            // Arrange
            val refreshTokenValue = "valid-refresh-token"

            every { revokeRefreshTokenUseCase.execute(any()) } just Runs

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .delete()
                .uri("${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE}")
                .cookie(Cookie("refreshToken", refreshTokenValue))
                .exchange()

            // Assert
            val request = slot<String>()
            verify(exactly = 1) { revokeRefreshTokenUseCase.execute(capture(request)) }
            val capturedRequest = request.captured
            assertEquals(refreshTokenValue, capturedRequest)

            assertThat(testResult)
                .hasStatus(HttpStatus.NO_CONTENT)
        }
    }

    @Nested
    inner class RevokeAllRefreshTokens {
        @Test
        fun `revokeAll endpoint should revoke all refresh tokens and return no content`() {
            // Arrange
            every { revokeAllRefreshTokensUseCase.execute() } just Runs

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .delete()
                .uri("${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE_ALL}")
                .exchange()

            // Assert
            verify(exactly = 1) { revokeAllRefreshTokensUseCase.execute() }

            assertThat(testResult)
                .hasStatus(HttpStatus.NO_CONTENT)
        }
    }

}