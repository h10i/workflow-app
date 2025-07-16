package com.example.workflow.unit.feature.token.presenter

import com.example.workflow.core.account.Account
import com.example.workflow.core.token.RefreshToken
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.presenter.RefreshTokenPresenter
import com.example.workflow.feature.token.usecase.RefreshTokenUseCase
import com.example.workflow.support.annotation.UnitTest
import com.example.workflow.support.util.TestDataFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@UnitTest
class RefreshTokenPresenterTest {
    private lateinit var refreshTokenPresenter: RefreshTokenPresenter

    @BeforeEach
    fun setUp() {
        refreshTokenPresenter = RefreshTokenPresenter()
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
            val account: Account = TestDataFactory.createAccount()
            val refreshToken: RefreshToken = TestDataFactory.registerRefreshToken(account = account)
            val useCaseResult = RefreshTokenUseCase.Result(
                refreshToken = refreshToken,
                accessToken = accessToken,
            )
            val response = TokenResponse(accessToken)

            // Act
            val actual = refreshTokenPresenter.toResponse(useCaseResult)

            // Assert
            assertEquals(response, actual.response)
        }
    }
}