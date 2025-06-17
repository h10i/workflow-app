package com.example.workflow.feature.account.controller

import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.service.AccountService
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.assertj.MvcTestResult
import java.util.*

@WebMvcTest(AccountController::class)
@Import(AccountControllerApiTest.MockConfig::class)
class AccountControllerApiTest {
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    @Autowired
    private lateinit var accountService: AccountService

    @TestConfiguration
    class MockConfig {
        @Bean
        fun accountService(): AccountService = mockk()
    }

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    @WithMockUser
    fun `GET v1_accounts_me should return account information`() {
        // Arrange
        val accountId = UUID.randomUUID()
        val emailAddress = "user@example.com"
        val accountViewDto = AccountViewDto(
            id = accountId,
            emailAddress = emailAddress,
            roleNames = listOf("USER"),
        )

        every { accountService.getCurrentAccountId() } returns accountId
        every { accountService.getAccount(accountId) } returns accountViewDto

        // Act
        val testResult: MvcTestResult = mockMvcTester
            .get()
            .uri("/v1/accounts/me")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // Assert
        assertThat(testResult)
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(
                """
                {
                    "id": "${accountId}",
                    "emailAddress": "$emailAddress", 
                    "roleNames": ["USER"]
                }
            """.trimIndent()
            )
    }
}