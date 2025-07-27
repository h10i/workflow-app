package com.example.workflow.unit.feature.account.usecase

import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.account.usecase.GetAccountUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
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
        @Test
        fun `execute method should return account`() {
            // Arrange
            val accountId: UUID = UUID.randomUUID()
            val accountViewDto: AccountViewDto = mockk()

            every { accountServiceMock.getCurrentAccountId() } returns accountId
            every { accountServiceMock.getAccount(accountId) } returns accountViewDto

            // Act
            val actual: GetAccountUseCase.Result = getAccountUseCase.execute()

            // Assert
            assertEquals(accountViewDto, actual.accountViewDto)
        }
    }
}