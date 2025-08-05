package com.example.workflow.unit.feature.account.usecase

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.toViewDto
import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.account.usecase.GetAccountUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class GetAccountUseCaseTest {
    private lateinit var accountServiceMock: AccountService
    private lateinit var getAccountUseCase: GetAccountUseCase

    @BeforeEach
    fun setUp() {
        accountServiceMock = mockk()

        getAccountUseCase = GetAccountUseCase(accountServiceMock)
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ExecuteMethod {
        @BeforeEach
        fun setUp() {
            mockkStatic(Account::toViewDto)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(Account::toViewDto)
        }

        @Test
        fun `execute method should return account`() {
            // Arrange
            val accountId: UUID = UUID.randomUUID()
            val accountViewDtoMock: AccountViewDto = mockk()
            val accountMock: Account = mockk()

            every { accountServiceMock.getCurrentAccountId() } returns accountId
            every { accountServiceMock.getAccount(accountId) } returns accountMock
            every { accountMock.toViewDto() } returns accountViewDtoMock

            // Act
            val actual: GetAccountUseCase.Result = getAccountUseCase.execute()

            // Assert
            assertEquals(accountViewDtoMock, actual.accountViewDto)
        }
    }
}