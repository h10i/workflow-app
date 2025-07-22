package com.example.workflow.unit.feature.token.usecase

import com.example.workflow.core.token.RefreshToken
import com.example.workflow.feature.token.factory.RefreshTokenCookieFactory
import com.example.workflow.feature.token.model.TokenRequest
import com.example.workflow.feature.token.service.AuthenticationService
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.feature.token.service.TokenService
import com.example.workflow.feature.token.usecase.IssueTokenUseCase
import com.example.workflow.support.annotation.UnitTest
import com.example.workflow.support.util.TestDataFactory
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import java.util.*

@UnitTest
class IssueTokenUseCaseTest {
    private lateinit var authenticationServiceMock: AuthenticationService
    private lateinit var tokenServiceMock: TokenService
    private lateinit var refreshTokenServiceMock: RefreshTokenService
    private lateinit var refreshTokenCookieFactoryMock: RefreshTokenCookieFactory
    private lateinit var issueTokenUseCase: IssueTokenUseCase

    @BeforeEach
    fun setUp() {
        authenticationServiceMock = mockk()
        tokenServiceMock = mockk()
        refreshTokenServiceMock = mockk()
        refreshTokenCookieFactoryMock = mockk()

        issueTokenUseCase = IssueTokenUseCase(
            authenticationService = authenticationServiceMock,
            tokenService = tokenServiceMock,
            refreshTokenService = refreshTokenServiceMock,
            refreshTokenCookieFactory = refreshTokenCookieFactoryMock,
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ExecuteMethod {
        @Test
        fun `execute method should return access token and refresh token`() {
            // Arrange
            val emailAddress = "user@example.com"
            val password = "test-password"
            val account = TestDataFactory.createAccount(
                emailAddress = emailAddress,
                password = password,
            )
            val request = TokenRequest(emailAddress, password)
            val authentication: Authentication = TestDataFactory.createAuthentication(account)
            val refreshToken: RefreshToken = TestDataFactory.registerRefreshToken(account = account)
            val responseCookie = ResponseCookie.from("refreshToken", "dummy").build()
            val accessToken = "test-access-token"

            every { authenticationServiceMock.authenticate(emailAddress, password) } returns authentication
            every {
                tokenServiceMock.generateToken(
                    userId = authentication.name,
                    scope = authentication.authorities.map { it.authority.toString() }
                )
            } returns accessToken
            every {
                refreshTokenServiceMock.createRefreshToken(UUID.fromString(authentication.name))
            } returns refreshToken
            every { refreshTokenCookieFactoryMock.create(refreshToken.value) } returns responseCookie

            // Act
            val actual: IssueTokenUseCase.Result = issueTokenUseCase.execute(request)

            // Assert
            assertEquals(accessToken, actual.accessToken)
            assertEquals(responseCookie, actual.refreshTokenCookie)
        }
    }

}