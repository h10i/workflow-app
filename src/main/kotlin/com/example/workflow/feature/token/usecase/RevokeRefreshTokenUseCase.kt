package com.example.workflow.feature.token.usecase

import com.example.workflow.feature.account.service.AccountService
import com.example.workflow.feature.token.service.RefreshTokenService
import org.springframework.stereotype.Service
import java.util.*

@Service
class RevokeRefreshTokenUseCase(
    private val accountService: AccountService,
    private val refreshTokenService: RefreshTokenService,
) {
    fun execute(refreshTokenValue: String) {
        val accountId: UUID = accountService.getCurrentAccountId()
        refreshTokenService.revokeRefreshToken(accountId, refreshTokenValue)
    }
}