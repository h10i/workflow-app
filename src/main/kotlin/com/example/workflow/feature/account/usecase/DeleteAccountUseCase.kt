package com.example.workflow.feature.account.usecase

import com.example.workflow.feature.account.service.AccountService
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeleteAccountUseCase(
    private val accountService: AccountService
) {
    fun execute() {
        val accountId: UUID = accountService.getCurrentAccountId()
        accountService.deleteAccountById(accountId)
    }
}