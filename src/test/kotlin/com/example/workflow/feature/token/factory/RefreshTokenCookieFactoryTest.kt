package com.example.workflow.feature.token.factory

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RefreshTokenCookieFactoryTest {
    private lateinit var refreshTokenCookieFactory: RefreshTokenCookieFactory

    @BeforeEach
    fun setUp() {
        refreshTokenCookieFactory = RefreshTokenCookieFactory()
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class Create() {
        @Test
        fun `create method should generate a ResponseCookie with correct properties for the given value`() {
            // Arrange
            val refreshTokenValue = "test-refresh-token-value"
            val expectedMaxAge = Duration.ofSeconds(30L * 24 * 60 * 60)

            // Act
            val actual = refreshTokenCookieFactory.create(refreshTokenValue)

            // Assert
            assertEquals("refreshToken", actual.name)
            assertEquals(refreshTokenValue, actual.value)
            assertTrue(actual.isHttpOnly)
            assertTrue(actual.isSecure)
            assertEquals("/v1/auth/refresh-token", actual.path)
            assertEquals("localhost", actual.domain)
            assertEquals(expectedMaxAge, actual.maxAge)
            assertEquals("None", actual.sameSite)
        }
    }

}