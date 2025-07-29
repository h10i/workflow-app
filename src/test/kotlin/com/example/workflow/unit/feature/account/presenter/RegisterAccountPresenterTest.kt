package com.example.workflow.unit.feature.account.presenter

import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.toViewResponse
import com.example.workflow.feature.account.presenter.RegisterAccountPresenter
import com.example.workflow.feature.account.usecase.RegisterAccountUseCase
import com.example.workflow.support.annotation.UnitTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class RegisterAccountPresenterTest {
    private lateinit var registerAccountPresenter: RegisterAccountPresenter

    @BeforeEach
    fun setUp() {
        registerAccountPresenter = RegisterAccountPresenter()
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ToResponse {
        @Test
        fun `toResponse returns presenter result`() {
            // Arrange
            val id = UUID.randomUUID()
            val emailAddress = "user@example.com"
            val roleNames = listOf("USER", "ADMIN")
            val accountViewDto = AccountViewDto(
                id = id,
                emailAddress = emailAddress,
                roleNames = roleNames,
            )
            val expectedResponse = accountViewDto.toViewResponse()
            val useCaseResult = RegisterAccountUseCase.Result(
                accountViewDto = accountViewDto,
            )

            // Act
            val actual: RegisterAccountPresenter.Result = registerAccountPresenter.toResponse(useCaseResult)

            // Assert
            assertEquals(expectedResponse, actual.response)
        }
    }

}