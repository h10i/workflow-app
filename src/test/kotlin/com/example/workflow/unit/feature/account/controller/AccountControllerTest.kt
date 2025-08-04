package com.example.workflow.unit.feature.account.controller

import com.example.workflow.feature.account.controller.AccountController
import com.example.workflow.feature.account.model.*
import com.example.workflow.feature.account.presenter.GetAccountPresenter
import com.example.workflow.feature.account.presenter.RegisterAccountPresenter
import com.example.workflow.feature.account.presenter.UpdateAccountPresenter
import com.example.workflow.feature.account.usecase.DeleteAccountUseCase
import com.example.workflow.feature.account.usecase.GetAccountUseCase
import com.example.workflow.feature.account.usecase.RegisterAccountUseCase
import com.example.workflow.feature.account.usecase.UpdateAccountUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNull

@UnitTest
class AccountControllerTest {
    private lateinit var registerAccountUseCase: RegisterAccountUseCase
    private lateinit var registerAccountPresenter: RegisterAccountPresenter
    private lateinit var getAccountUseCase: GetAccountUseCase
    private lateinit var getAccountPresenter: GetAccountPresenter
    private lateinit var updateAccountUseCase: UpdateAccountUseCase
    private lateinit var updateAccountPresenter: UpdateAccountPresenter
    private lateinit var deleteAccountUseCase: DeleteAccountUseCase
    private lateinit var accountController: AccountController

    @BeforeEach
    fun setUp() {
        registerAccountUseCase = mockk()
        registerAccountPresenter = mockk()
        getAccountUseCase = mockk()
        getAccountPresenter = mockk()
        updateAccountUseCase = mockk()
        updateAccountPresenter = mockk()
        deleteAccountUseCase = mockk()
        accountController = AccountController(
            registerAccountUseCase = registerAccountUseCase,
            registerAccountPresenter = registerAccountPresenter,
            getAccountUseCase = getAccountUseCase,
            getAccountPresenter = getAccountPresenter,
            updateAccountUseCase = updateAccountUseCase,
            updateAccountPresenter = updateAccountPresenter,
            deleteAccountUseCase = deleteAccountUseCase,
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class RegisterAccount() {
        @BeforeEach
        fun setUp() {
            mockkStatic(AccountViewDto::toViewResponse)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(AccountViewDto::toViewResponse)
        }

        @Test
        fun `registerAccount should return account view response`() {
            // Arrange
            val request = RegisterAccountRequest(
                emailAddress = "user@example.com",
                password = "test-password",
            )
            val accountViewDtoMock = mockk<AccountViewDto>()
            val useCaseResult = RegisterAccountUseCase.Result(
                accountViewDto = accountViewDtoMock,
            )

            val accountViewResponseMock: AccountViewResponse = mockk()
            val presenterResult = RegisterAccountPresenter.Result(
                response = accountViewResponseMock
            )

            every { registerAccountUseCase.execute(request) } returns useCaseResult
            every { registerAccountPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val actual = accountController.registerAccount(request)

            // Assert
            assertEquals(HttpStatus.CREATED, actual.statusCode)
            assertEquals(accountViewResponseMock, actual.body)
        }
    }

    @Nested
    inner class Get() {
        @BeforeEach
        fun setUp() {
            mockkStatic(AccountViewDto::toViewResponse)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(AccountViewDto::toViewResponse)
        }

        @Test
        fun `get should return account view response`() {
            // Arrange
            val accountViewDtoMock = mockk<AccountViewDto>()
            val useCaseResult = GetAccountUseCase.Result(
                accountViewDto = accountViewDtoMock,
            )

            val accountViewResponseMock = mockk<AccountViewResponse>()
            val presenterResult = GetAccountPresenter.Result(
                response = accountViewResponseMock
            )

            every { getAccountUseCase.execute() } returns useCaseResult
            every { getAccountPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val actual = accountController.get()

            // Assert
            assertEquals(HttpStatus.OK, actual.statusCode)
            assertEquals(accountViewResponseMock, actual.body)
        }
    }

    @Nested
    inner class UpdateAccount() {
        @BeforeEach
        fun setUp() {
            mockkStatic(AccountViewDto::toViewResponse)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(AccountViewDto::toViewResponse)
        }

        @Test
        fun `returns account view response`() {
            // Arrange
            val request = UpdateAccountRequest(
                emailAddress = "new@example.com",
                password = "new-test-password",
            )
            val accountViewDtoMock = mockk<AccountViewDto>()
            val useCaseResult = UpdateAccountUseCase.Result(
                accountViewDto = accountViewDtoMock,
            )

            val accountViewResponseMock: AccountViewResponse = mockk()
            val presenterResult = UpdateAccountPresenter.Result(
                response = accountViewResponseMock
            )

            every { updateAccountUseCase.execute(request) } returns useCaseResult
            every { updateAccountPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val actual = accountController.updateAccount(request)

            // Assert
            assertEquals(HttpStatus.OK, actual.statusCode)
            assertEquals(accountViewResponseMock, actual.body)
        }
    }

    @Nested
    inner class DeleteAccount {
        @Test
        fun `returns no content`() {
            // Arrange
            every { deleteAccountUseCase.execute() } just runs

            // Act
            val actual = accountController.deleteAccount()

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, actual.statusCode)
            assertNull(actual.body)
        }
    }
}