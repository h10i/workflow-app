package com.example.workflow.core.token

import com.example.workflow.core.account.Account
import com.example.workflow.test.annotation.IntegrationTest
import com.example.workflow.test.config.AssertJComparisonConfig
import com.example.workflow.test.util.TestDataFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@IntegrationTest
@DataJpaTest
class RefreshTokenRepositoryTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class FindByValue {
        private lateinit var account: Account

        @BeforeEach
        fun setUp() {
            // Arrange
            account = TestDataFactory.createAccount()
            entityManager.persist(account)

            val refreshToken = TestDataFactory.registerRefreshToken(account = account)
            entityManager.persist(refreshToken)

            entityManager.flush()
            entityManager.clear()
        }

        @AfterEach
        fun tearDown() {
        }

        @Test
        fun `return RefreshToken when token value exists`() {
            // Arrange
            val refreshToken = account.refreshTokens[0]
            val refreshTokenValue = refreshToken.value

            // Act
            val actual: RefreshToken? = refreshTokenRepository.findByValue(refreshTokenValue)

            // Assert
            assertNotNull(actual)
            assertThat(actual)
                .usingRecursiveComparison(AssertJComparisonConfig.INSTANT_TRUNCATED_TO_MILLIS)
                .isEqualTo(refreshToken)
        }

        @Test
        fun `return null when token value exists`() {
            // Arrange
            val refreshTokenValue = UUID.randomUUID().toString()

            // Act
            val actual: RefreshToken? = refreshTokenRepository.findByValue(refreshTokenValue)

            // Assert
            assertNull(actual)
        }
    }

    @Nested
    inner class DeleteByAccountIdAndValue {
        private val accounts: MutableList<Account> = mutableListOf()

        @BeforeEach
        fun setUp() {
            // Arrange
            for (i in 0..1) {
                val account = TestDataFactory.createAccount(emailAddress = "user${i}@example.com")
                accounts.add(account)
                entityManager.persist(account)

                (0..1).forEach { j ->
                    val refreshToken: RefreshToken = TestDataFactory.registerRefreshToken(account = account)
                    entityManager.persist(refreshToken)
                }
            }

            entityManager.flush()
            entityManager.clear()
        }

        @AfterEach
        fun tearDown() {
        }

        @Test
        fun `return 1 when account ID and value are correct`() {
            // Arrange
            val accountId = accounts[0].id
            val refreshTokenValue = accounts[0].refreshTokens[0].value

            // Act
            val actual: Int = refreshTokenRepository.deleteByAccountIdAndValue(
                accountId = accountId,
                value = refreshTokenValue,
            )

            // Assert
            assertEquals(1, actual)
        }

        @Test
        fun `return 0 when token value is from another account`() {
            // Arrange
            val accountId = accounts[1].id
            val refreshTokenValue = accounts[0].refreshTokens[0].value

            // Act
            val actual: Int = refreshTokenRepository.deleteByAccountIdAndValue(
                accountId = accountId,
                value = refreshTokenValue,
            )

            // Assert
            assertEquals(0, actual)
        }
    }

    @Nested
    inner class DeleteByAccountId {
        private lateinit var account: Account

        @BeforeEach
        fun setUp() {
            // Arrange
            account = TestDataFactory.createAccount()
            entityManager.persist(account)

            (0..1).forEach { j ->
                val refreshToken: RefreshToken = TestDataFactory.registerRefreshToken(account = account)
                entityManager.persist(refreshToken)
            }

            entityManager.flush()
            entityManager.clear()
        }

        @AfterEach
        fun tearDown() {
        }

        @Test
        fun `return number of refresh tokens when account ID exists`() {
            // Arrange
            val accountId = account.id

            // Act
            val actual: Int = refreshTokenRepository.deleteByAccountId(
                accountId = accountId,
            )

            // Assert
            assertEquals(account.refreshTokens.size, actual)
        }

        @Test
        fun `return 0 when account ID doesn't exists`() {
            // Arrange
            val accountId = UUID.randomUUID()

            // Act
            val actual: Int = refreshTokenRepository.deleteByAccountId(
                accountId = accountId,
            )

            // Assert
            assertEquals(0, actual)
        }
    }
}