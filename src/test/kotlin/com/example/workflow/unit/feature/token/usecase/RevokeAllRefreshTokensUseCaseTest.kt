package com.example.workflow.unit.feature.token.usecase

import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.token.service.RefreshTokenService
import com.example.workflow.feature.token.usecase.RevokeAllRefreshTokensUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

    @Nested
    inner class ExecuteFun {
        @Test
        fun `should revoke all refresh token`() {
            // Arrange
            val accountId = UUID.randomUUID()

            every { accountServiceMock.getCurrentAccountId() } returns accountId
            every { refreshTokenServiceMock.revokeAllRefreshTokens(accountId) } returns 1

            // Act
            revokeRefreshTokenUseCase.execute()

            // Assert
            verify { refreshTokenServiceMock.revokeAllRefreshTokens(accountId) }
        }
    }
}
