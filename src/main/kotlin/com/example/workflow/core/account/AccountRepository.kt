package com.example.workflow.core.account

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AccountRepository : JpaRepository<Account, UUID> {
    fun findByEmailAddress(emailAddress: String): Account?
}