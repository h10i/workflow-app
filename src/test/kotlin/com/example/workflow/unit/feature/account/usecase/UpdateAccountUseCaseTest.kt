package com.example.workflow.unit.feature.account.usecase

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.toViewDto
import com.example.workflow.feature.account.exception.EmailAddressAlreadyRegisteredException
import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.UpdateAccountRequest
import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.account.usecase.UpdateAccountUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.*
import org.junit.jupiter.api.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class UpdateAccountUseCaseTest {
    private lateinit var accountService: AccountService
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var updateAccountUseCase: UpdateAccountUseCase

    @BeforeEach
    fun setUp() {
        accountService = mockk()
        passwordEncoder = mockk()
        updateAccountUseCase = UpdateAccountUseCase(
            accountService = accountService,
            passwordEncoder = passwordEncoder,
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ExecuteMethod {
        private val originalAccountId = UUID.randomUUID()
        private val originalEmailAddress = "original@example.com"
        private val originalEncryptedPassword = "original-encrypted-password"

        @BeforeEach
        fun setUp() {
            mockkStatic(Account::toViewDto)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(Account::toViewDto)
        }

        @Test
        fun `should not update any fields when request has no values`() {
            // Arrange
            val newEmailAddress = "new@example.com"

            withUpdatedAccountTest(
                request = UpdateAccountRequest(),
                mockAdditional = {
                    every { accountService.verifyEmailAddressAvailability(newEmailAddress) } just runs
                },
                assert = { updated ->
                    assertEquals(originalEmailAddress, updated.emailAddress)
                    assertEquals(originalEncryptedPassword, updated.password)
                },
            )
        }

        @Test
        fun `should update email address when email address is provided`() {
            // Arrange
            val newEmailAddress = "new@example.com"

            withUpdatedAccountTest(
                request = UpdateAccountRequest(emailAddress = newEmailAddress),
                mockAdditional = {
                    every { accountService.verifyEmailAddressAvailability(newEmailAddress) } just runs
                },
                assert = { updated ->
                    assertEquals(newEmailAddress, updated.emailAddress)
                    assertEquals(originalEncryptedPassword, updated.password)
                },
            )
        }

        @Test
        fun `should update password when password is provided`() {
            // Arrange
            val newPassword = "new-password"
            val newEncryptedPassword = "new-encrypted-password"

            withUpdatedAccountTest(
                request = UpdateAccountRequest(password = newPassword),
                mockAdditional = {
                    every { passwordEncoder.encode(newPassword) } returns newEncryptedPassword
                },
                assert = { updated ->
                    assertEquals(originalEmailAddress, updated.emailAddress)
                    assertEquals(newEncryptedPassword, updated.password)
                },
            )
        }

        @Test
        fun `throws EmailAddressAlreadyRegisteredException when new email address is already registered`() {
            // Arrange
            val newEmailAddress = "new@example.com"
            val newPassword = "new-password"
            val request = UpdateAccountRequest(
                emailAddress = newEmailAddress,
                password = newPassword,
            )

            val originalAccount = Account(
                id = originalAccountId,
                emailAddress = originalEmailAddress,
                password = originalEncryptedPassword,
            )

            every { accountService.getCurrentAccountId() } returns originalAccountId
            every { accountService.getAccount(originalAccountId) } returns originalAccount
            every { accountService.verifyEmailAddressAvailability(newEmailAddress) } throws EmailAddressAlreadyRegisteredException()

            // Act
            // Assert
            assertThrows<EmailAddressAlreadyRegisteredException> {
                updateAccountUseCase.execute(request)
            }
        }

        private fun withUpdatedAccountTest(
            request: UpdateAccountRequest,
            setup: (Account) -> Unit = {},
            mockAdditional: () -> Unit = {},
            assert: (Account) -> Unit
        ) {
            // Arrange
            val originalAccount = Account(
                id = originalAccountId,
                emailAddress = originalEmailAddress,
                password = originalEncryptedPassword,
            )

            setup(originalAccount)

            val savedAccount: Account = mockk()
            val accountViewDto: AccountViewDto = mockk()

            val capturedAccount = slot<Account>()

            every { accountService.getCurrentAccountId() } returns originalAccountId
            every { accountService.getAccount(originalAccountId) } returns originalAccount
            every { accountService.saveAccount(capture(capturedAccount)) } returns savedAccount
            every { savedAccount.toViewDto() } returns accountViewDto

            mockAdditional()

            // Act
            val result = updateAccountUseCase.execute(request)

            // Assert
            assert(capturedAccount.captured)
            assertEquals(accountViewDto, result.accountViewDto)
        }
    }
}