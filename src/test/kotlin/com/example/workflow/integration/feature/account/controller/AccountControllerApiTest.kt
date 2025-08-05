package com.example.workflow.integration.feature.account.controller

import com.example.workflow.common.path.ApiPath
import com.example.workflow.feature.account.controller.AccountController
import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.account.model.RegisterAccountRequest
import com.example.workflow.feature.account.model.UpdateAccountRequest
import com.example.workflow.feature.account.presenter.GetAccountPresenter
import com.example.workflow.feature.account.presenter.RegisterAccountPresenter
import com.example.workflow.feature.account.presenter.UpdateAccountPresenter
import com.example.workflow.feature.account.usecase.DeleteAccountUseCase
import com.example.workflow.feature.account.usecase.GetAccountUseCase
import com.example.workflow.feature.account.usecase.RegisterAccountUseCase
import com.example.workflow.feature.account.usecase.UpdateAccountUseCase
import com.example.workflow.integration.test.config.NoSecurityConfig
import com.example.workflow.support.annotation.IntegrationTest
import io.mockk.*
import org.assertj.core.api.Assertions
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
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.assertj.MvcTestResult
import java.util.*
import kotlin.test.assertEquals

@IntegrationTest
@WebMvcTest(AccountController::class)
@Import(AccountControllerApiTest.MockConfig::class, NoSecurityConfig::class)
class AccountControllerApiTest {
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    @Autowired
    private lateinit var registerAccountUseCase: RegisterAccountUseCase

    @Autowired
    private lateinit var registerAccountPresenter: RegisterAccountPresenter

    @Autowired
    private lateinit var getAccountUseCase: GetAccountUseCase

    @Autowired
    private lateinit var getAccountPresenter: GetAccountPresenter

    @Autowired
    private lateinit var updateAccountUseCase: UpdateAccountUseCase

    @Autowired
    private lateinit var updateAccountPresenter: UpdateAccountPresenter

    @Autowired
    private lateinit var deleteAccountUseCase: DeleteAccountUseCase

    @TestConfiguration
    class MockConfig {
        @Bean
        fun registerAccountUseCase(): RegisterAccountUseCase = mockk()

        @Bean
        fun registerAccountPresenter(): RegisterAccountPresenter = mockk()

        @Bean
        fun getAccountUseCase(): GetAccountUseCase = mockk()

        @Bean
        fun getAccountPresenter(): GetAccountPresenter = mockk()

        @Bean
        fun updateAccountUseCase(): UpdateAccountUseCase = mockk()

        @Bean
        fun updateAccountPresenter(): UpdateAccountPresenter = mockk()

        @Bean
        fun deleteAccountUseCase(): DeleteAccountUseCase = mockk()
    }

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    inner class RegisterAccount {
        @Test
        fun `POST v1_accounts should return registered account information with valid request`() {
            // Arrange
            val emailAddress = "user@example.com"
            val password = "test-password"

            val accountViewDtoMock: AccountViewDto = mockk()
            val useCaseResult = RegisterAccountUseCase.Result(
                accountViewDto = accountViewDtoMock
            )

            val accountId: UUID = UUID.randomUUID()
            val accountViewResponse = AccountViewResponse(
                id = accountId,
                emailAddress = emailAddress,
                roleNames = listOf("USER"),
            )
            val presenterResult = RegisterAccountPresenter.Result(
                response = accountViewResponse
            )

            every { registerAccountUseCase.execute(any()) } returns useCaseResult
            every { registerAccountPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .post()
                .uri(ApiPath.Account.BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                    "emailAddress": "$emailAddress",
                    "password": "$password"
                    }
                    """.trimIndent()
                )
                .exchange()

            // Assert
            val request = slot<RegisterAccountRequest>()
            verify { registerAccountUseCase.execute(capture(request)) }
            val capturedRequest = request.captured
            assertEquals(emailAddress, capturedRequest.emailAddress)
            assertEquals(password, capturedRequest.password)

            Assertions.assertThat(testResult)
                .hasStatus(HttpStatus.CREATED)
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

        @Test
        fun `POST v1_accounts should return errors with invalid request`() {
            // Arrange
            val emailAddress = "user@example.com"
            val password = ""

            val accountViewDtoMock: AccountViewDto = mockk()
            val useCaseResult = RegisterAccountUseCase.Result(
                accountViewDto = accountViewDtoMock
            )

            val accountId: UUID = UUID.randomUUID()
            val accountViewResponse = AccountViewResponse(
                id = accountId,
                emailAddress = emailAddress,
                roleNames = listOf("USER"),
            )
            val presenterResult = RegisterAccountPresenter.Result(
                response = accountViewResponse
            )

            every { registerAccountUseCase.execute(any()) } returns useCaseResult
            every { registerAccountPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .post()
                .uri(ApiPath.Account.BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                    "emailAddress": "$emailAddress",
                    "password": "$password"
                    }
                    """.trimIndent()
                )
                .exchange()

            // Assert
            Assertions.assertThat(testResult)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .isLenientlyEqualTo(
                    """
                    {
                        "errors": {
                            "password": [
                                "password must not be blank"
                            ]
                        }
                    }
                    """.trimIndent()
                )
        }
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
            Assertions.assertThat(testResult)
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

    @Nested
    inner class UpdateAccount {
        @Test
        fun `PATCH v1_accounts_me should return updated account information with valid request`() {
            // Arrange
            val emailAddress = "new@example.com"
            val password = "new-test-password"

            val accountViewDtoMock: AccountViewDto = mockk()
            val useCaseResult = UpdateAccountUseCase.Result(
                accountViewDto = accountViewDtoMock
            )

            val accountId: UUID = UUID.randomUUID()
            val accountViewResponse = AccountViewResponse(
                id = accountId,
                emailAddress = emailAddress,
                roleNames = listOf("USER"),
            )
            val presenterResult = UpdateAccountPresenter.Result(
                response = accountViewResponse
            )

            every { updateAccountUseCase.execute(any()) } returns useCaseResult
            every { updateAccountPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .patch()
                .uri("${ApiPath.Account.BASE}${ApiPath.Account.ME}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                    "emailAddress": "$emailAddress",
                    "password": "$password"
                    }
                    """.trimIndent()
                )
                .exchange()

            // Assert
            val request = slot<UpdateAccountRequest>()
            verify { updateAccountUseCase.execute(capture(request)) }
            val capturedRequest = request.captured
            assertEquals(emailAddress, capturedRequest.emailAddress)
            assertEquals(password, capturedRequest.password)

            Assertions.assertThat(testResult)
                .hasStatus(HttpStatus.OK)
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

        @Test
        fun `PATCH v1_accounts_me should return errors with invalid request`() {
            // Arrange
            val emailAddress = "user!example.com"
            val password = ""

            val accountViewDtoMock: AccountViewDto = mockk()
            val useCaseResult = UpdateAccountUseCase.Result(
                accountViewDto = accountViewDtoMock
            )

            val accountId: UUID = UUID.randomUUID()
            val accountViewResponse = AccountViewResponse(
                id = accountId,
                emailAddress = emailAddress,
                roleNames = listOf("USER"),
            )
            val presenterResult = UpdateAccountPresenter.Result(
                response = accountViewResponse
            )

            every { updateAccountUseCase.execute(any()) } returns useCaseResult
            every { updateAccountPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .patch()
                .uri("${ApiPath.Account.BASE}${ApiPath.Account.ME}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                    "emailAddress": "$emailAddress",
                    "password": "$password"
                    }
                    """.trimIndent()
                )
                .exchange()

            // Assert
            Assertions.assertThat(testResult)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .isLenientlyEqualTo(
                    """
                    {
                        "errors": {
                            "emailAddress": [
                                "Invalid email address format"
                            ]
                        }
                    }
                    """.trimIndent()
                )
        }
    }

    @Nested
    inner class DeleteAccount {
        @Test
        fun `DELETE v1_accounts_me should return no content`() {
            // Arrange
            every { deleteAccountUseCase.execute() } just runs

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .delete()
                .uri("${ApiPath.Account.BASE}${ApiPath.Account.ME}")
                .exchange()

            // Assert
            verify(exactly = 1) { deleteAccountUseCase.execute() }
            assertThat(testResult).hasStatus(HttpStatus.NO_CONTENT)
        }
    }
}