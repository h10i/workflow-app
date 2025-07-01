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
class RevokeRefreshTokenUseCaseTest {
    private lateinit var accountServiceMock: AccountService
    private lateinit var refreshTokenServiceMock: RefreshTokenService
    private lateinit var revokeRefreshTokenUseCase: RevokeRefreshTokenUseCase

    @BeforeEach
    fun setUp() {
        accountServiceMock = mockk()
        refreshTokenServiceMock = mockk()
        revokeRefreshTokenUseCase = RevokeRefreshTokenUseCase(
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
            val tokenValue = "revoked-token"
            val accountId = UUID.randomUUID()

            every { accountServiceMock.getCurrentAccountId() } returns accountId
            every { refreshTokenServiceMock.revokeRefreshToken(accountId, tokenValue) } returns 1

            // Act
            val actual = revokeRefreshTokenUseCase.execute(tokenValue)

            // Assert
            verify { refreshTokenServiceMock.revokeRefreshToken(accountId, tokenValue) }
        }
    }

}