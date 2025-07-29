package com.example.workflow.unit.feature.account.controller

import com.example.workflow.feature.account.controller.AccountController
import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.account.model.RegisterAccountRequest
import com.example.workflow.feature.account.model.toViewResponse
import com.example.workflow.feature.account.presenter.GetAccountPresenter
import com.example.workflow.feature.account.presenter.RegisterAccountPresenter
import com.example.workflow.feature.account.usecase.GetAccountUseCase
import com.example.workflow.feature.account.usecase.RegisterAccountUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@UnitTest
class AccountControllerTest {
    private lateinit var registerAccountUseCase: RegisterAccountUseCase
    private lateinit var registerAccountPresenter: RegisterAccountPresenter
    private lateinit var getAccountUseCase: GetAccountUseCase
    private lateinit var getAccountPresenter: GetAccountPresenter
    private lateinit var accountController: AccountController

    @BeforeEach
    fun setUp() {
        registerAccountUseCase = mockk()
        registerAccountPresenter = mockk()
        getAccountUseCase = mockk()
        getAccountPresenter = mockk()
        accountController = AccountController(
            registerAccountUseCase = registerAccountUseCase,
            registerAccountPresenter = registerAccountPresenter,
            getAccountUseCase = getAccountUseCase,
            getAccountPresenter = getAccountPresenter,
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
}