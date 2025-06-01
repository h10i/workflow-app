package com.example.workflow.service

import com.example.workflow.model.AccountViewDto
import com.example.workflow.model.toViewDto
import com.example.workflow.repository.AccountRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountService(
    private val accountRepository: AccountRepository
) {
    fun getCurrentAccountId(): UUID {
        return UUID.fromString(SecurityContextHolder.getContext().authentication.name)
    }

    fun getAccount(id: UUID): AccountViewDto {
        return accountRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Account not found: $id") }
            .toViewDto()
    }
}