package com.example.workflow.unit.feature.auth.presenter

import com.example.workflow.core.account.Account
import com.example.workflow.core.auth.RefreshToken
import com.example.workflow.feature.auth.model.TokenResponse
import com.example.workflow.feature.auth.presenter.RefreshTokenPresenter
import com.example.workflow.feature.auth.usecase.RefreshTokenUseCase
import com.example.workflow.support.annotation.UnitTest
import com.example.workflow.support.util.TestDataFactory
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

    @Nested
    inner class ToResponseFun {
        @Test
        fun `should return a presenter result`() {
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
