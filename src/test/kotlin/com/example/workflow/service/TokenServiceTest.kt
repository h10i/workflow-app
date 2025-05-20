package com.example.workflow.service

import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class TokenServiceTest {
    private lateinit var jwtEncoderMock: JwtEncoder
    private lateinit var tokenService: TokenService

    @BeforeEach
    fun setUp() {
        jwtEncoderMock = mockk()
        tokenService = spyk(TokenService(jwtEncoderMock))
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class GenerateToken {
        @Test
        fun `generateToken should encode JWT claims and return token value`() {
            // Arrange
            val tokenValue = "mocked.jwt.token"
            val mockJwt = mockk<Jwt>()
            every { mockJwt.tokenValue } returns tokenValue

            val claimsSlot = slot<JwtEncoderParameters>()
            every { jwtEncoderMock.encode(capture(claimsSlot)) } returns mockJwt

            val fixedInstant = Instant.parse("2025-01-01T12:00:00Z")
            val fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC)
            val userId = "user-123"
            val scope = listOf("read", "write")

            // Act
            val result = tokenService.generateToken(fixedClock, userId, scope)

            // Assert
            assertEquals(tokenValue, result)
            verify(exactly = 1) { jwtEncoderMock.encode(any()) }

            val claimsSet = claimsSlot.captured.claims
            assertEquals("self", claimsSet.claims["iss"])
            assertEquals(fixedInstant, claimsSet.issuedAt)
            assertEquals(fixedInstant.plus(30, ChronoUnit.MINUTES), claimsSet.expiresAt)
            assertEquals(userId, claimsSet.subject)
            assertEquals("read write", claimsSet.claims["scope"])
        }
    }

}