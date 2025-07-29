package com.example.workflow.feature.account.service

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.AccountRepository
import com.example.workflow.core.account.toViewDto
import com.example.workflow.feature.account.model.AccountViewDto
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AccountService(
    private val accountRepository: AccountRepository
) {
    @Transactional
    fun saveAccount(account: Account): AccountViewDto {
        return accountRepository.save(account)
            .toViewDto()
    }

    fun getCurrentAccountId(): UUID {
        return UUID.fromString(SecurityContextHolder.getContext().authentication.name)
    }

    @Transactional
    fun getAccount(id: UUID): AccountViewDto {
        return accountRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Account not found: $id") }
            .toViewDto()
    }
}