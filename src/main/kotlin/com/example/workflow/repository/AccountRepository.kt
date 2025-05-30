package com.example.workflow.repository

import com.example.workflow.model.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AccountRepository : JpaRepository<Account, UUID> {
    fun findByMailAddress(mailAddress: String): Account?
}