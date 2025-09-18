package com.example.workflow.core.auth

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID> {
    fun findByValue(value: String): RefreshToken?
    fun deleteByAccountIdAndValue(accountId: UUID, value: String): Int
    fun deleteByAccountId(accountId: UUID): Int
}
