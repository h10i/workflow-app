package com.example.workflow.unit.feature.token.service

import com.example.workflow.feature.token.service.AuthenticationService
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

@UnitTest
class AuthenticationServiceTest {
    private lateinit var authenticationManager: AuthenticationManager
    private lateinit var authenticationService: AuthenticationService

    @BeforeEach
    fun setUp() {
        authenticationManager = mockk()
        authenticationService = AuthenticationService(authenticationManager)
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class Authenticate {
        @Test
        fun `returns Authentication when credentials are valid`() {
            // Arrange
            val emailAddress = "user@example.com"
            val password = "securepassword"
            val expectedAuth = mockk<Authentication>()

            every {
                authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(emailAddress, password)
                )
            } returns expectedAuth

            // Act
            val result = authenticationService.authenticate(emailAddress, password)

            // Assert
            Assertions.assertEquals(expectedAuth, result)
        }

        @Test
        fun `throws AuthenticationException when credentials are invalid`() {
            // Arrange
            val emailAddress = "user@example.com"
            val password = "wrongpassword"

            every {
                authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(emailAddress, password)
                )
            } throws BadCredentialsException("Invalid credentials")

            // Act
            // Assert
            assertThrows<AuthenticationException> {
                authenticationService.authenticate(emailAddress, password)
            }
        }
    }

}