package com.example.workflow.unit.feature.account.usecase

import com.example.workflow.core.account.Account
import com.example.workflow.feature.account.exception.EmailAddressAlreadyRegisteredException
import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.RegisterAccountRequest
import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.account.usecase.RegisterAccountUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@UnitTest
class RegisterAccountUseCaseTest {
    private lateinit var accountServiceMock: AccountService
    private lateinit var registerAccountUseCase: RegisterAccountUseCase

    @BeforeEach
    fun setUp() {
        accountServiceMock = mockk()
        registerAccountUseCase = RegisterAccountUseCase(accountServiceMock)
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ExecuteMethod {
        @Test
        fun `return account with valid request`() {
            // Arrange
            val request = RegisterAccountRequest(
                emailAddress = "user@example.com",
                password = "test-password",
            )
            val accountViewDto: AccountViewDto = mockk()
            val claimsSlot = slot<Account>()

            every { accountServiceMock.getAccount(request.emailAddress) } returns null
            every { accountServiceMock.saveAccount(capture(claimsSlot)) } returns accountViewDto

            // Act
            val actual: RegisterAccountUseCase.Result = registerAccountUseCase.execute(request)

            // Assert
            val claimsSet = claimsSlot.captured
            assertEquals(request.emailAddress, claimsSet.emailAddress)
            assertEquals(request.password, claimsSet.password)

            assertEquals(accountViewDto, actual.accountViewDto)
        }

        @Test
        fun `throw EmailAlreadyRegisteredException with registered email address`() {
            // Arrange
            val request = RegisterAccountRequest(
                emailAddress = "user@example.com",
                password = "test-password",
            )
            val registeredAccount: AccountViewDto = mockk()

            every { accountServiceMock.getAccount(request.emailAddress) } returns registeredAccount

            // Act
            // Assert
            val actualException = assertThrows<EmailAddressAlreadyRegisteredException> {
                registerAccountUseCase.execute(request)
            }
            assertEquals("This email address is already registered.", actualException.message)

        }
    }
}