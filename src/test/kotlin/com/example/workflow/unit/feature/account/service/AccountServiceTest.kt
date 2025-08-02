package com.example.workflow.unit.feature.account.service

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.AccountRepository
import com.example.workflow.core.account.toViewDto
import com.example.workflow.feature.account.exception.EmailAddressAlreadyRegisteredException
import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.support.annotation.UnitTest
import io.mockk.*
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

@UnitTest
class AccountServiceTest {
    private lateinit var accountService: AccountService
    private lateinit var accountRepositoryMock: AccountRepository

    @BeforeEach
    fun setUp() {
        accountRepositoryMock = mockk()
        accountService = AccountService(accountRepositoryMock)
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class SaveAccount {
        @BeforeEach
        fun setUp() {
            mockkStatic(Account::toViewDto)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(Account::toViewDto)
        }

        @Test
        fun `returns account when creating a new account`() {
            // Arrange
            val accountMock: Account = mockk()
            val savedAccountMock: Account = mockk()
            val expectedAccountViewDtoMock: AccountViewDto = mockk()

            every { accountRepositoryMock.save(accountMock) } returns savedAccountMock
            every { savedAccountMock.toViewDto() } returns expectedAccountViewDtoMock

            // Act
            val actual = accountService.saveAccount(accountMock)

            // Assert
            assertEquals(savedAccountMock, actual)
        }
    }

    @Nested
    inner class GetCurrentAccountId {
        @BeforeEach
        fun setUp() {
            mockkStatic(SecurityContextHolder::class)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(SecurityContextHolder::class)
        }

        @Test
        fun `returns account id from security context`() {
            // Arrange
            val expectedAccountId: UUID = UUID.randomUUID()
            val securityContextMock: SecurityContext = mockk()
            val authenticationMock: Authentication = mockk()

            every { SecurityContextHolder.getContext() } returns securityContextMock
            every { securityContextMock.authentication } returns authenticationMock
            every { authenticationMock.name } returns expectedAccountId.toString()

            // Act
            val actualAccountId = accountService.getCurrentAccountId()

            // Assert
            assertEquals(expectedAccountId, actualAccountId)
        }
    }

    @Nested
    inner class GetAccountById {
        @Test
        fun `returns account when account exists`() {
            // Arrange
            val accountId: UUID = UUID.randomUUID()
            val accountMock: Account = mockk()

            every { accountRepositoryMock.findById(accountId) } returns Optional.of(accountMock)

            // Act
            val actualAccount: Account = accountService.getAccount(accountId)

            // Assert
            assertEquals(accountMock, actualAccount)
        }

        @Test
        fun `throws EntityNotFoundException when account doesn't exists`() {
            // Arrange
            val accountId: UUID = UUID.randomUUID()
            every { accountRepositoryMock.findById(accountId) } returns Optional.empty()

            // Act
            // Assert
            val actualException = assertThrows<EntityNotFoundException> {
                accountService.getAccount(accountId)
            }
            assertEquals("Account not found: $accountId", actualException.message)
        }
    }

    @Nested
    inner class GetAccountByEmailAddress {
        @Test
        fun `returns account when account exists`() {
            // Arrange
            val emailAddress = "user@example.com"
            val accountMock: Account = mockk()

            every { accountRepositoryMock.findByEmailAddress(emailAddress) } returns accountMock

            // Act
            val actual: Account? = accountService.getAccountByEmailAddress(emailAddress)

            // Assert
            assertEquals(accountMock, actual)
        }

        @Test
        fun `returns null when account doesn't exists`() {
            // Arrange
            val emailAddress = "user@example.com"
            every { accountRepositoryMock.findByEmailAddress(emailAddress) } returns null

            // Act
            val actual: Account? = accountService.getAccountByEmailAddress(emailAddress)

            // Assert
            assertNull(actual)
        }
    }

    @Nested
    inner class VerifyEmailAddressAvailability {
        @Test
        fun `throws EmailAddressAlreadyRegisteredException when email address is registered`() {
            // Arrange
            val emailAddress = "user@example.com"
            val accountMock: Account = mockk()
            every { accountRepositoryMock.findByEmailAddress(emailAddress) } returns accountMock

            // Act
            // Assert
            val actualException = assertThrows<EmailAddressAlreadyRegisteredException> {
                accountService.verifyEmailAddressAvailability(emailAddress)
            }
            assertEquals(Account::emailAddress.name, actualException.field)
            assertEquals("This email address is already registered.", actualException.message)
        }

        @Test
        fun `does not throws EmailAddressAlreadyRegisteredException when email address is not registered`() {
            // Arrange
            val emailAddress = "user@example.com"
            every { accountRepositoryMock.findByEmailAddress(emailAddress) } returns null

            // Act
            // Assert
            assertDoesNotThrow {
                accountService.verifyEmailAddressAvailability(emailAddress)
            }
            verify(exactly = 1) { accountRepositoryMock.findByEmailAddress(emailAddress) }
        }
    }
}