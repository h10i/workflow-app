package com.example.workflow.unit.feature.account.usecase

import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.account.usecase.DeleteAccountUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class DeleteAccountUseCaseTest {
    private lateinit var accountService: AccountService
    private lateinit var deleteAccountUseCase: DeleteAccountUseCase

    @BeforeEach
    fun setUp() {
        accountService = mockk()
        deleteAccountUseCase = DeleteAccountUseCase(
            accountService = accountService,
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ExecuteMethod {
        @Test
        fun `should delete account by authenticated id`() {
            // Arrange
            val accountId: UUID = UUID.randomUUID()
            every { accountService.getCurrentAccountId() } returns accountId

            val claimsSlot = slot<UUID>()
            every { accountService.deleteAccountById(capture(claimsSlot)) } just runs

            // Act
            deleteAccountUseCase.execute()

            // Assert
            val capturedAccountId = claimsSlot.captured
            assertEquals(accountId, capturedAccountId)
        }
    }
}