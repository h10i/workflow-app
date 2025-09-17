package com.example.workflow.unit.feature.token.service

import com.example.workflow.feature.token.service.TokenService
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
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

@UnitTest
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
    inner class GenerateTokenFun {
        @Test
        fun `should encode JWT claims and return token value`() {
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
            Assertions.assertEquals(tokenValue, result)
            verify(exactly = 1) { jwtEncoderMock.encode(any()) }

            val claimsSet = claimsSlot.captured.claims
            Assertions.assertEquals("self", claimsSet.claims["iss"])
            Assertions.assertEquals(fixedInstant, claimsSet.issuedAt)
            Assertions.assertEquals(fixedInstant.plus(30, ChronoUnit.MINUTES), claimsSet.expiresAt)
            Assertions.assertEquals(userId, claimsSet.subject)
            Assertions.assertEquals("read write", claimsSet.claims["scope"])
        }
    }
}
