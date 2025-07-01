package com.example.workflow.feature.account.controller

import com.example.workflow.common.path.ApiPath
import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.account.presenter.GetAccountPresenter
import com.example.workflow.feature.account.usecase.GetAccountUseCase
import com.example.workflow.test.annotation.IntegrationTest
import com.example.workflow.test.config.NoSecurityConfig
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.assertj.MvcTestResult
import java.util.*

@IntegrationTest
@WebMvcTest(AccountController::class)
@Import(AccountControllerApiTest.MockConfig::class, NoSecurityConfig::class)
class AccountControllerApiTest {
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    @Autowired
    private lateinit var getAccountUseCase: GetAccountUseCase

    @Autowired
    private lateinit var getAccountPresenter: GetAccountPresenter

    @TestConfiguration
    class MockConfig {
        @Bean
        fun getAccountUseCase(): GetAccountUseCase = mockk()

        @Bean
        fun getAccountPresenter(): GetAccountPresenter = mockk()
    }

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    inner class Get {
        @Test
        fun `GET v1_accounts_me should return account information`() {
            // Arrange
            val accountId = UUID.randomUUID()
            val emailAddress = "user@example.com"

            val accountViewDtoMock = mockk<AccountViewDto>()
            val useCaseResult = GetAccountUseCase.Result(
                accountViewDto = accountViewDtoMock,
            )

            val accountViewResponse = AccountViewResponse(
                id = accountId,
                emailAddress = emailAddress,
                roleNames = listOf("USER"),
            )
            val presenterResult = GetAccountPresenter.Result(
                response = accountViewResponse
            )

            every { getAccountUseCase.execute() } returns useCaseResult
            every { getAccountPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .get()
                .uri("${ApiPath.Account.BASE}${ApiPath.Account.ME}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

            // Assert
            assertThat(testResult)
                .hasStatusOk()
                .bodyJson()
                .isLenientlyEqualTo(
                    """
                {
                    "id": "$accountId",
                    "emailAddress": "$emailAddress", 
                    "roleNames": ["USER"]
                }
                """.trimIndent()
                )
        }
    }
}