package com.example.workflow.feature.token.service

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.AccountRepository
import com.example.workflow.core.token.RefreshToken
import com.example.workflow.core.token.RefreshTokenRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val accountRepository: AccountRepository,
) {
    @Transactional
    fun createRefreshToken(accountId: UUID): RefreshToken {
        val account: Account =
            accountRepository.findById(accountId)
                .orElseThrow { EntityNotFoundException("Account not found: $accountId") }

        val refreshToken = RefreshToken(
            value = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plus(90, ChronoUnit.DAYS),
            account = account,
        )
        return refreshTokenRepository.save(refreshToken)
    }

    fun getRefreshTokenByValue(value: String): RefreshToken? {
        return refreshTokenRepository.findByValue(value)
    }

    @Transactional
    fun verifyExpiration(refreshToken: RefreshToken): RefreshToken? {
        if (refreshToken.expiryDate < Instant.now()) {
            refreshTokenRepository.delete(refreshToken)
            return null
        }
        return refreshToken
    }

    @Transactional
    fun revokeRefreshToken(accountId: UUID, value: String): Int {
        return refreshTokenRepository.deleteByAccountIdAndValue(accountId, value)
    }

    @Transactional
    fun revokeAllRefreshTokens(accountId: UUID): Int {
        return refreshTokenRepository.deleteByAccountId(accountId)
    }
}
