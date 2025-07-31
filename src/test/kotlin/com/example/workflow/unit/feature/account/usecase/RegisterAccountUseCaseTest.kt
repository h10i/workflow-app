package com.example.workflow.unit.feature.account.usecase

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.toViewDto
import com.example.workflow.feature.account.exception.EmailAddressAlreadyRegisteredException
import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.RegisterAccountRequest
import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.account.usecase.RegisterAccountUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.*
import org.junit.jupiter.api.*
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
        fun `return account with valid request`() {
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
            every { accountServiceMock.getAccountViewDto(request.emailAddress) } returns null
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
        fun `throw EmailAlreadyRegisteredException with registered email address`() {
            // Arrange
            val request = RegisterAccountRequest(
                emailAddress = "user@example.com",
                password = "test-password",
            )

            every { accountServiceMock.verifyEmailAddressAvailability(request.emailAddress) } throws EmailAddressAlreadyRegisteredException()

            // Act
            // Assert
            val actualException = assertThrows<EmailAddressAlreadyRegisteredException> {
                registerAccountUseCase.execute(request)
            }
            assertEquals("This email address is already registered.", actualException.message)

        }
    }
}