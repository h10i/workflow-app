package com.example.workflow.unit.feature.token.presenter

import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.presenter.TokenPresenter
import com.example.workflow.feature.token.usecase.IssueTokenUseCase
import com.example.workflow.support.annotation.UnitTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseCookie
import kotlin.test.assertEquals

@UnitTest
class TokenPresenterTest {
    private lateinit var tokenPresenter: TokenPresenter

    @BeforeEach
    fun setUp() {
        tokenPresenter = TokenPresenter()
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ToResponse {
        @Test
        fun `toResponse returns presenter result`() {
            // Arrange
            val accessToken = "test-access-token"
            val refreshTokenCookie = ResponseCookie.from("refreshToken", "dummy").build()
            val useCaseResult = IssueTokenUseCase.Result(
                accessToken = accessToken,
                refreshTokenCookie = refreshTokenCookie,
            )
            val response = TokenResponse(accessToken)

            // Act
            val actual = tokenPresenter.toResponse(useCaseResult)

            // Assert
            assertEquals(response, actual.response)
            assertEquals(refreshTokenCookie, actual.refreshTokenCookie)
        }
    }


}