package com.example.workflow.feature.account.usecase

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.toViewDto
import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.service.AccountService
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetAccountUseCase(
    private val accountService: AccountService,
) {
    data class Result(
        val accountViewDto: AccountViewDto
    )

    fun execute(): Result {
        val accountId: UUID = accountService.getCurrentAccountId()
        val account: Account = accountService.getAccount(accountId)
        return Result(
            accountViewDto = account.toViewDto()
        )
    }
}