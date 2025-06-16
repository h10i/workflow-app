package com.example.workflow.feature.token.controller

import com.example.workflow.core.token.RefreshToken
import com.example.workflow.feature.token.factory.RefreshTokenCookieFactory
import com.example.workflow.feature.token.model.TokenRequest
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.service.AuthenticationService
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.feature.token.service.TokenService
import com.example.workflow.testutil.TestDataFactory
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
import org.springframework.security.core.Authentication
import java.util.*
import kotlin.test.assertEquals

class TokenControllerTest {
    private lateinit var authenticationServiceMock: AuthenticationService
    private lateinit var tokenServiceMock: TokenService
    private lateinit var refreshTokenServiceMock: RefreshTokenService
    private lateinit var refreshTokenCookieFactoryMock: RefreshTokenCookieFactory
    private lateinit var tokenController: TokenController

    @BeforeEach
    fun setUp() {
        authenticationServiceMock = mockk()
        tokenServiceMock = mockk()
        refreshTokenServiceMock = mockk()
        refreshTokenCookieFactoryMock = mockk()
        tokenController = TokenController(
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
    inner class TokenMethod {
        @Test
        fun `token endpoint should return token and set refreshToken cookie`() {
            // Arrange
            val emailAddress = "user@example.com"
            val password = "test-password"
            val account = TestDataFactory.createAccount(
                emailAddress = emailAddress,
                password = password,
            )
            val request = TokenRequest(emailAddress, password)
            val response: HttpServletResponse = mockk(relaxed = true)
            val authentication: Authentication = TestDataFactory.createAuthentication(account)
            val refreshToken: RefreshToken = TestDataFactory.createRefreshToken()
            val responseCookie = ResponseCookie.from("refreshToken", "dummy").build()
            val token = "test-access-token"
            val expectedTokenResponse = TokenResponse(token)

            every { authenticationServiceMock.authenticate(emailAddress, password) } returns authentication
            every {
                tokenServiceMock.generateToken(
                    userId = authentication.name,
                    scope = authentication.authorities.map { it.authority.toString() }
                )
            } returns token
            every {
                refreshTokenServiceMock.createRefreshToken(UUID.fromString(authentication.name))
            } returns refreshToken
            every { refreshTokenCookieFactoryMock.create(refreshToken.value) } returns responseCookie

            // Act
            val actual: ResponseEntity<TokenResponse> = tokenController.token(request, response)

            // Assert
            verify { response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString()) }
            assertEquals(HttpStatus.OK, actual.statusCode)
            assertEquals(expectedTokenResponse, actual.body)
        }
    }

}