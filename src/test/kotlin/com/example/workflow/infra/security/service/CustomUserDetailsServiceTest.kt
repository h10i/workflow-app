package com.example.workflow.infra.security.service

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.AccountRepository
import com.example.workflow.core.account.AccountRole
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.*
import org.springframework.security.core.userdetails.UsernameNotFoundException
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
        fun `returns UserDetails when mail address exists`() {
            // Arrange
            val mailAddress = "user@example.com"
            val password = "testpassword"
            val accountRoles: MutableList<AccountRole> = mockk()
            val account: Account = Account(mailAddress = mailAddress, password = password, roles = accountRoles)
            every {
                accountRepository.findByMailAddress(mailAddress)
            } returns account

            // Act
            val result = customUserDetailsService.loadUserByUsername(mailAddress)

            // Assert
            assertEquals(mailAddress, result.username)
            assertEquals(password, result.password)
            assertEquals(accountRoles, result.authorities)
        }

        @Test
        fun `throws UsernameNotFoundException when mail address not exists`() {
            // Arrange
            val mailAddress = "user@example.com"
            every {
                accountRepository.findByMailAddress(mailAddress)
            } returns null

            // Act
            // Assert
            val exception = assertThrows<UsernameNotFoundException> {
                customUserDetailsService.loadUserByUsername(mailAddress)
            }
            assertEquals("User not found: $mailAddress", exception.message)
        }
    }

}