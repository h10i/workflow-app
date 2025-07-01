package com.example.workflow.feature.token.controller

import com.example.workflow.feature.token.model.TokenRequest
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.presenter.TokenPresenter
import com.example.workflow.feature.token.usecase.IssueTokenUseCase
import com.example.workflow.test.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals

@UnitTest
class TokenControllerTest {
    private lateinit var issueTokenUseCase: IssueTokenUseCase
    private lateinit var tokenPresenter: TokenPresenter
    private lateinit var tokenController: TokenController

    @BeforeEach
    fun setUp() {
        issueTokenUseCase = mockk()
        tokenPresenter = mockk()
        tokenController = TokenController(
            issueTokenUseCase = issueTokenUseCase,
            tokenPresenter = tokenPresenter,
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class TokenMethod {
        @Test
        fun `token endpoint should return token and set refreshToken cookie`() {
            // Arrange
            val request = TokenRequest("user@example.com", "test-password")
            val response: HttpServletResponse = mockk(relaxed = true)
            val responseCookie = ResponseCookie.from("refreshToken", "dummy").build()
            val accessToken = "test-access-token"
            val tokenResponse = TokenResponse(accessToken)
            val useCaseResult = IssueTokenUseCase.Result(accessToken, responseCookie)
            val presenterResult = TokenPresenter.Result(
                response = tokenResponse,
                refreshTokenCookie = responseCookie,
            )

            every { issueTokenUseCase.execute(request) } returns useCaseResult
            every { tokenPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val actual: ResponseEntity<TokenResponse> = tokenController.token(request, response)

            // Assert
            verify { response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString()) }
            assertEquals(HttpStatus.OK, actual.statusCode)
            assertEquals(tokenResponse, actual.body)
        }
    }

}