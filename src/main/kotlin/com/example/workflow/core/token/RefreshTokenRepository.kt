package com.example.workflow.core.token

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID> {
    fun findByValue(value: String): RefreshToken?
}
