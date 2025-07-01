package com.example.workflow.feature.token.usecase

import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.test.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

@UnitTest
class RevokeAllRefreshTokensUseCaseTest {
    private lateinit var accountServiceMock: AccountService
    private lateinit var refreshTokenServiceMock: RefreshTokenService
    private lateinit var revokeRefreshTokenUseCase: RevokeAllRefreshTokensUseCase

    @BeforeEach
    fun setUp() {
        accountServiceMock = mockk()
        refreshTokenServiceMock = mockk()
        revokeRefreshTokenUseCase = RevokeAllRefreshTokensUseCase(
            accountService = accountServiceMock,
            refreshTokenService = refreshTokenServiceMock
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ExecuteMethod {
        @Test
        fun `revoke refresh token succeeds`() {
            // Arrange
            val accountId = UUID.randomUUID()

            every { accountServiceMock.getCurrentAccountId() } returns accountId
            every { refreshTokenServiceMock.revokeAllRefreshTokens(accountId) } returns 1

            // Act
            val actual = revokeRefreshTokenUseCase.execute()

            // Assert
            verify { refreshTokenServiceMock.revokeAllRefreshTokens(accountId) }
        }
    }

}