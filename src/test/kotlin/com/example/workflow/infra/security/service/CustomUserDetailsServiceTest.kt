package com.example.workflow.infra.security.service

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.AccountRepository
import com.example.workflow.core.account.AccountRole
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.*
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.*
import kotlin.test.assertEquals

class CustomUserDetailsServiceTest {
    private lateinit var accountRepository: AccountRepository
    private lateinit var customUserDetailsService: CustomUserDetailsService

    @BeforeEach
    fun setUp() {
        accountRepository = mockk()
        customUserDetailsService = CustomUserDetailsService(accountRepository)
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class LoadUserByUsername {
        @Test
        fun `returns UserDetails when email address exists`() {
            // Arrange
            val id = UUID.randomUUID()
            val emailAddress = "user@example.com"
            val password = "testpassword"
            val accountRoles: MutableList<AccountRole> = mockk()
            val account: Account =
                Account(id = id, emailAddress = emailAddress, password = password, roles = accountRoles)
            every {
                accountRepository.findByEmailAddress(emailAddress)
            } returns account

            // Act
            val result = customUserDetailsService.loadUserByUsername(emailAddress)

            // Assert
            assertEquals(id.toString(), result.username)
            assertEquals(password, result.password)
            assertEquals(accountRoles, result.authorities)
        }

        @Test
        fun `throws UsernameNotFoundException when email address not exists`() {
            // Arrange
            val emailAddress = "user@example.com"
            every {
                accountRepository.findByEmailAddress(emailAddress)
            } returns null

            // Act
            // Assert
            val exception = assertThrows<UsernameNotFoundException> {
                customUserDetailsService.loadUserByUsername(emailAddress)
            }
            assertEquals("Account not found: $emailAddress", exception.message)
        }
    }

}