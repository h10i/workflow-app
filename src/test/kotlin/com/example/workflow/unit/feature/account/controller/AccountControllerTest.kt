package com.example.workflow.unit.feature.account.controller

import com.example.workflow.feature.account.controller.AccountController
import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.account.model.RegisterAccountRequest
import com.example.workflow.feature.account.model.UpdateAccountRequest
import com.example.workflow.feature.account.presenter.AccountPresenter
import com.example.workflow.feature.account.usecase.DeleteAccountUseCase
import com.example.workflow.feature.account.usecase.GetAccountUseCase
import com.example.workflow.feature.account.usecase.RegisterAccountUseCase
import com.example.workflow.feature.account.usecase.UpdateAccountUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNull

@UnitTest
class AccountControllerTest {
    private lateinit var registerAccountUseCase: RegisterAccountUseCase
    private lateinit var getAccountUseCase: GetAccountUseCase
    private lateinit var updateAccountUseCase: UpdateAccountUseCase
    private lateinit var deleteAccountUseCase: DeleteAccountUseCase
    private lateinit var accountPresenter: AccountPresenter
    private lateinit var accountController: AccountController

    @BeforeEach
    fun setUp() {
        registerAccountUseCase = mockk()
        getAccountUseCase = mockk()
        updateAccountUseCase = mockk()
        deleteAccountUseCase = mockk()
        accountPresenter = mockk()
        accountController = AccountController(
            registerAccountUseCase = registerAccountUseCase,
            getAccountUseCase = getAccountUseCase,
            updateAccountUseCase = updateAccountUseCase,
            deleteAccountUseCase = deleteAccountUseCase,
            accountPresenter = accountPresenter,
        )
    }

    @Nested
    inner class RegisterAccountFun {
        @Test
        fun `should execute RegisterAccountUseCase and return account view response`() {
            // Arrange
            val request: RegisterAccountRequest = mockk()
            val useCaseResult: RegisterAccountUseCase.Result = mockk(relaxed = true)
            val accountViewResponseMock: AccountViewResponse = mockk()
            val presenterResult = AccountPresenter.Result(
                response = accountViewResponseMock
            )

            every { registerAccountUseCase.execute(request) } returns useCaseResult
            every { accountPresenter.toResponse(useCaseResult.accountViewDto) } returns presenterResult

            // Act
            val actual = accountController.registerAccount(request)

            // Assert
            assertEquals(HttpStatus.CREATED, actual.statusCode)
            assertEquals(accountViewResponseMock, actual.body)
        }
    }

    @Nested
    inner class GetAccountFun {
        @Test
        fun `should execute GetAccountUseCase and return account view response`() {
            // Arrange
            val useCaseResult: GetAccountUseCase.Result = mockk(relaxed = true)
            val accountViewResponseMock = mockk<AccountViewResponse>()
            val presenterResult = AccountPresenter.Result(
                response = accountViewResponseMock
            )

            every { getAccountUseCase.execute() } returns useCaseResult
            every { accountPresenter.toResponse(useCaseResult.accountViewDto) } returns presenterResult

            // Act
            val actual = accountController.getAccount()

            // Assert
            assertEquals(HttpStatus.OK, actual.statusCode)
            assertEquals(accountViewResponseMock, actual.body)
        }
    }

    @Nested
    inner class UpdateAccount {
        @Test
        fun `should execute UpdateAccountUseCase and return account view response`() {
            // Arrange
            val request: UpdateAccountRequest = mockk()
            val useCaseResult: UpdateAccountUseCase.Result = mockk(relaxed = true)

            val accountViewResponseMock: AccountViewResponse = mockk()
            val presenterResult = AccountPresenter.Result(
                response = accountViewResponseMock
            )

            every { updateAccountUseCase.execute(request) } returns useCaseResult
            every { accountPresenter.toResponse(useCaseResult.accountViewDto) } returns presenterResult

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
        fun `should execute DeleteAccountUseCase and return no content`() {
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
