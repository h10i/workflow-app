package com.example.workflow.feature.token.controller

import com.example.workflow.feature.token.model.TokenRequest
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.presenter.TokenPresenter
import com.example.workflow.feature.token.usecase.IssueTokenUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
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
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.assertj.MvcTestResult
import kotlin.test.assertEquals

@WebMvcTest(TokenController::class)
@Import(TokenControllerApiTest.MockConfig::class)
class TokenControllerApiTest {
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    @Autowired
    private lateinit var issueTokenUseCase: IssueTokenUseCase

    @Autowired
    private lateinit var tokenPresenter: TokenPresenter

    @TestConfiguration
    class MockConfig {
        @Bean
        fun issueTokenUseCase(): IssueTokenUseCase = mockk(relaxed = true)

        @Bean
        fun tokenPresenter(): TokenPresenter = mockk(relaxed = true)
    }

    @BeforeEach
    fun setUp() {
        mockMvcTester.apply { springSecurity() }
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class TokenMethod {
        @Test
        @WithMockUser
        fun `GET v1_auth_token should return access token and set cookie to refresh token`() {
            // Arrange
            val emailAddress = "user@example.com"
            val password = "P4sSw0rd!"
            val accessToken = "test-access-token"
            val refreshToken = "test-refresh-token"
            val responseCookie = ResponseCookie.from("refreshToken", refreshToken).build()
            val useCaseResult = IssueTokenUseCase.Result(
                accessToken,
                responseCookie,
            )
            val presenterResult = TokenPresenter.Result(
                TokenResponse(accessToken),
                responseCookie,
            )

            every { issueTokenUseCase.execute(any()) } returns useCaseResult
            every { tokenPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .post()
                .uri("/v1/auth/token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                    "emailAddress": "$emailAddress",
                    "password": "$password"
                    }
                    """.trimIndent()
                )
                .exchange()

            // Assert
            val request = slot<TokenRequest>()
            verify { issueTokenUseCase.execute(capture(request)) }
            val capturedRequest = request.captured
            assertEquals(emailAddress, capturedRequest.emailAddress)
            assertEquals(password, capturedRequest.password)

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
            assertThat(testResult).cookies().hasValue("refreshToken", refreshToken)
        }
    }
}