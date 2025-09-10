package com.example.workflow.unit.feature.token.service

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.AccountRepository
import com.example.workflow.core.token.RefreshToken
import com.example.workflow.core.token.RefreshTokenRepository
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.support.annotation.UnitTest
import io.mockk.*
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@UnitTest
class RefreshTokenServiceTest {
    private lateinit var refreshTokenRepositoryMock: RefreshTokenRepository
    private lateinit var accountRepositoryMock: AccountRepository
    private lateinit var refreshTokenService: RefreshTokenService

    @BeforeEach
    fun setUp() {
        refreshTokenRepositoryMock = mockk()
        accountRepositoryMock = mockk()
        refreshTokenService = RefreshTokenService(
            refreshTokenRepositoryMock,
            accountRepositoryMock,
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class CreateRefreshTokenFun {
        @Test
        fun `should return RefreshToken when an account exists`() {
            // Arrange
            val accountId: UUID = UUID.randomUUID()
            val accountMock: Account = mockk()

            every { accountRepositoryMock.findById(accountId) } returns Optional.of(accountMock)
            every { refreshTokenRepositoryMock.save(any()) } answers { firstArg() }

            // Act
            val actual = refreshTokenService.createRefreshToken(accountId)

            // Assert
            assertEquals(accountMock, actual.account)
            assertNotNull(actual.expiryDate)
            assertTrue(actual.expiryDate > Instant.now())
        }

        @Test
        fun `should throw EntityNotFoundException when an account does not exist`() {
            // Arrange
            val accountId: UUID = UUID.randomUUID()

            every { accountRepositoryMock.findById(accountId) } returns Optional.empty()

            // Act
            // Assert
            val actualException = assertThrows<EntityNotFoundException> {
                refreshTokenService.createRefreshToken(accountId)
            }
            assertEquals("Account not found: $accountId", actualException.message)
        }
    }

    @Nested
    inner class GetRefreshTokenByValueFun {
        @Test
        fun `should return refresh token when a value exists`() {
            // Arrange
            val refreshTokenValue: String = UUID.randomUUID().toString()
            val expectedRefreshToken: RefreshToken = mockk()

            every { refreshTokenRepositoryMock.findByValue(refreshTokenValue) } returns expectedRefreshToken

            // Act
            val actual: RefreshToken? = refreshTokenService.getRefreshTokenByValue(refreshTokenValue)

            // Assert
            assertEquals(expectedRefreshToken, actual)
        }

        @Test
        fun `should return refresh token when a value does not exist`() {
            // Arrange
            val refreshTokenValue: String = UUID.randomUUID().toString()

            every { refreshTokenRepositoryMock.findByValue(refreshTokenValue) } returns null

            // Act
            val actual: RefreshToken? = refreshTokenService.getRefreshTokenByValue(refreshTokenValue)

            // Assert
            assertNull(actual)
        }
    }

    @Nested
    inner class VerifyExpirationFun {
        @Test
        fun `should return refresh token when not expired token`() {
            // Arrange
            val refreshToken: RefreshToken = mockk()
            val futureDate = Instant.now().plus(1, ChronoUnit.DAYS)

            every { refreshToken.expiryDate } returns futureDate

            // Act
            val actual = refreshTokenService.verifyExpiration(refreshToken)

            // Assert
            assertEquals(refreshToken, actual)
        }

        @Test
        fun `should return null when expired token`() {
            // Arrange
            val refreshToken: RefreshToken = mockk()
            val pastDate = Instant.now().minus(1, ChronoUnit.DAYS)

            every { refreshToken.expiryDate } returns pastDate
            every { refreshTokenRepositoryMock.delete(refreshToken) } just Runs

            // Act
            val actual = refreshTokenService.verifyExpiration(refreshToken)

            // Assert
            verify(exactly = 1) { refreshTokenRepositoryMock.delete(refreshToken) }
            assertNull(actual)
        }
    }

    @Nested
    inner class RevokeRefreshTokenFun {
        @Test
        fun `should return deleted count`() {
            // Arrange
            val accountId: UUID = UUID.randomUUID()
            val refreshTokenValue: String = UUID.randomUUID().toString()
            val expectedCount = 1

            every {
                refreshTokenRepositoryMock.deleteByAccountIdAndValue(
                    accountId,
                    refreshTokenValue
                )
            } returns expectedCount

            // Act
            val actual: Int = refreshTokenService.revokeRefreshToken(accountId, refreshTokenValue)

            // Assert
            assertEquals(expectedCount, actual)
        }
    }

    @Nested
    inner class RevokeAllRefreshTokensFun {
        @Test
        fun `should return deleted count`() {
            // Arrange
            val accountId: UUID = UUID.randomUUID()
            val expectedCount = 2

            every {
                refreshTokenRepositoryMock.deleteByAccountId(
                    accountId,
                )
            } returns expectedCount

            // Act
            val actual: Int = refreshTokenService.revokeAllRefreshTokens(accountId)

            // Assert
            assertEquals(expectedCount, actual)
        }
    }
}