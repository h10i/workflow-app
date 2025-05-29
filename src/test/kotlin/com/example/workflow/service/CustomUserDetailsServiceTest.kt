package com.example.workflow.service

import com.example.workflow.model.AuthUser
import com.example.workflow.repository.AuthUserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.*
import org.springframework.security.core.userdetails.UsernameNotFoundException
import kotlin.test.assertEquals

class CustomUserDetailsServiceTest {
    private lateinit var authUserRepository: AuthUserRepository
    private lateinit var customUserDetailsService: CustomUserDetailsService

    @BeforeEach
    fun setUp() {
        authUserRepository = mockk()
        customUserDetailsService = CustomUserDetailsService(authUserRepository)
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
            val authUser: AuthUser = mockk()
            every {
                authUserRepository.findByMailAddress(mailAddress)
            } returns authUser

            // Act
            val result = customUserDetailsService.loadUserByUsername(mailAddress)

            // Assert
            assertEquals(authUser, result)
        }

        @Test
        fun `throws UsernameNotFoundException when mail address not exists`() {
            // Arrange
            val mailAddress = "user@example.com"
            every {
                authUserRepository.findByMailAddress(mailAddress)
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