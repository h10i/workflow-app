package com.example.workflow.feature.account.controller

import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.account.model.toViewResponse
import com.example.workflow.feature.account.service.AccountService
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.test.assertEquals

class AccountControllerTest {
    private lateinit var accountServiceMock: AccountService
    private lateinit var accountController: AccountController

    @BeforeEach
    fun setUp() {
        accountServiceMock = mockk()
        accountController = AccountController(accountServiceMock)
    }

    @AfterEach
    fun tearDown() {
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
            val accountId = UUID.randomUUID()
            val accountViewDtoMock = mockk<AccountViewDto>()
            val accountViewResponseMock = mockk<AccountViewResponse>()

            every { accountServiceMock.getCurrentAccountId() } returns accountId
            every { accountServiceMock.getAccount(accountId) } returns accountViewDtoMock
            every { accountViewDtoMock.toViewResponse() } returns accountViewResponseMock

            // Act
            val response = accountController.get()

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(accountViewResponseMock, response.body)
        }
    }
}