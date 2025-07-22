package com.example.workflow.unit.core.account

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.AccountRepository
import com.example.workflow.support.annotation.IntegrationTest
import com.example.workflow.support.config.AssertJComparisonConfig
import com.example.workflow.support.util.TestDataFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@IntegrationTest
@DataJpaTest
class AccountRepositoryTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class FindByEmailAddress {
        private lateinit var account: Account

        @BeforeEach
        fun setUp() {
            // Arrange
            val role = TestDataFactory.createRole()
            entityManager.persist(role)

            account = TestDataFactory.createAccount()
            entityManager.persist(account)

            val accountRole = TestDataFactory.registerAccountRole(account = account, role = role)
            entityManager.persist(accountRole)

            val refreshToken = TestDataFactory.registerRefreshToken(account = account)
            entityManager.persist(refreshToken)

            entityManager.flush()
            entityManager.clear()
        }

        @AfterEach
        fun tearDown() {
        }

        @Test
        fun `return Account when email address exists`() {
            // Arrange
            val emailAddress = account.emailAddress

            // Act
            val actual: Account? = accountRepository.findByEmailAddress(emailAddress)

            // Assert
            assertNotNull(actual)
            assertThat(actual)
                .usingRecursiveComparison(AssertJComparisonConfig.INSTANT_TRUNCATED_TO_MILLIS)
                .isEqualTo(account)
        }

        @Test
        fun `return null when email address doesn't exists`() {
            // Arrange
            val emailAddress = "not.found.${account.emailAddress}"

            // Act
            val actual: Account? = accountRepository.findByEmailAddress(emailAddress)

            // Assert
            assertNull(actual)
        }
    }
}