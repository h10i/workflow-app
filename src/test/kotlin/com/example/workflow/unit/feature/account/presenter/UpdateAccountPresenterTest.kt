package com.example.workflow.unit.feature.account.presenter

import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.toViewResponse
import com.example.workflow.feature.account.presenter.UpdateAccountPresenter
import com.example.workflow.feature.account.usecase.UpdateAccountUseCase
import com.example.workflow.support.annotation.UnitTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class UpdateAccountPresenterTest {
    private lateinit var updateAccountPresenter: UpdateAccountPresenter

    @BeforeEach
    fun setUp() {
        updateAccountPresenter = UpdateAccountPresenter()
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
            val useCaseResult = UpdateAccountUseCase.Result(
                accountViewDto = accountViewDto,
            )

            // Act
            val actual: UpdateAccountPresenter.Result = updateAccountPresenter.toResponse(useCaseResult)

            // Assert
            assertEquals(expectedResponse, actual.response)
        }
    }
}