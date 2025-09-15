package com.example.workflow.unit.feature.account.presenter

import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.account.model.toViewResponse
import com.example.workflow.feature.account.presenter.AccountPresenter
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@UnitTest
class AccountPresenterTest {
    private lateinit var accountPresenter: AccountPresenter

    @BeforeEach
    fun setUp() {
        accountPresenter = AccountPresenter()
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ToResponseFun {
        @BeforeEach
        fun setUp() {
            mockkStatic(AccountViewDto::toViewResponse)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(AccountViewDto::toViewResponse)
        }

        @Test
        fun `should return a presenter result`() {
            // Arrange
            val accountViewDto: AccountViewDto = mockk()
            val accountViewResponse: AccountViewResponse = mockk()

            every { accountViewDto.toViewResponse() } returns accountViewResponse

            // Act
            val actual = accountPresenter.toResponse(accountViewDto)

            // Assert
            assertEquals(accountViewResponse, actual.response)
        }
    }
}