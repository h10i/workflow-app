package com.example.workflow.unit.feature.account.usecase

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.toViewDto
import com.example.workflow.feature.account.exception.EmailAddressAlreadyRegisteredException
import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.RegisterAccountRequest
import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.account.usecase.RegisterAccountUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals

@UnitTest
class RegisterAccountUseCaseTest {
    private lateinit var accountServiceMock: AccountService
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var registerAccountUseCase: RegisterAccountUseCase

    @BeforeEach
    fun setUp() {
        accountServiceMock = mockk()
        passwordEncoder = mockk()
        registerAccountUseCase = RegisterAccountUseCase(
            accountService = accountServiceMock,
            passwordEncoder = passwordEncoder,
        )
    }

    @Nested
    inner class ExecuteFun {
        @BeforeEach
        fun setUp() {
            mockkStatic(Account::toViewDto)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(Account::toViewDto)
        }

        @Test
        fun `should return the account when valid request`() {
            // Arrange
            val request = RegisterAccountRequest(
                emailAddress = "user@example.com",
                password = "test-password",
            )
            val encryptedPassword = "encrypted-test-password"
            val savedAccount: Account = mockk()
            val accountViewDto: AccountViewDto = mockk()
            val claimsSlot = slot<Account>()

            every { accountServiceMock.verifyEmailAddressAvailability(request.emailAddress) } just runs
            every { passwordEncoder.encode(request.password) } returns encryptedPassword
            every { accountServiceMock.saveAccount(capture(claimsSlot)) } returns savedAccount
            every { savedAccount.toViewDto() } returns accountViewDto

            // Act
            val actual: RegisterAccountUseCase.Result = registerAccountUseCase.execute(request)

            // Assert
            val claimsSet = claimsSlot.captured
            assertEquals(request.emailAddress, claimsSet.emailAddress)
            assertEquals(encryptedPassword, claimsSet.password)

            assertEquals(accountViewDto, actual.accountViewDto)
        }

        @Test
        fun `should throw EmailAlreadyRegisteredException when email address is registered`() {
            // Arrange
            val request = RegisterAccountRequest(
                emailAddress = "user@example.com",
                password = "test-password",
            )

            every {
                accountServiceMock.verifyEmailAddressAvailability(request.emailAddress)
            } throws EmailAddressAlreadyRegisteredException()

            // Act
            // Assert
            assertThrows<EmailAddressAlreadyRegisteredException> {
                registerAccountUseCase.execute(request)
            }
        }
    }
}
