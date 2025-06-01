package com.example.workflow.service

import com.example.workflow.model.Account
import com.example.workflow.model.AccountViewDto
import com.example.workflow.model.toViewDto
import com.example.workflow.repository.AccountRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*
import kotlin.test.assertEquals

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
    inner class GetAccount {
        @BeforeEach
        fun setUp() {
            mockkStatic(Account::toViewDto)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(Account::toViewDto)
        }

        @Test
        fun `returns account view dto when account exists`() {
            // Arrange
            val accountId: UUID = UUID.randomUUID()
            val accountMock: Account = mockk()
            val expectedAccountViewDtoMock: AccountViewDto = mockk()

            every { accountRepositoryMock.findById(accountId) } returns Optional.of(accountMock)
            every { accountMock.toViewDto() } returns expectedAccountViewDtoMock

            // Act
            val actualAccountViewDto = accountService.getAccount(accountId)

            // Assert
            assertEquals(expectedAccountViewDtoMock, actualAccountViewDto)
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
}