package com.example.workflow.service

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

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
            val mailAddress = "user@example.com"
            val password = "securepassword"
            val expectedAuth = mockk<Authentication>()

            every {
                authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(mailAddress, password)
                )
            } returns expectedAuth

            // Act
            val result = authenticationService.authenticate(mailAddress, password)

            // Assert
            assertEquals(expectedAuth, result)
        }

        @Test
        fun `throws AuthenticationException when credentials are invalid`() {
            // Arrange
            val mailAddress = "user@example.com"
            val password = "wrongpassword"

            every {
                authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(mailAddress, password)
                )
            } throws BadCredentialsException("Invalid credentials")

            // Act
            // Assert
            assertThrows<AuthenticationException> {
                authenticationService.authenticate(mailAddress, password)
            }
        }
    }

}