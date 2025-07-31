package com.example.workflow.feature.account.usecase

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
        val accountViewDto: AccountViewDto = accountService.getAccountViewDto(accountId)
        return Result(
            accountViewDto = accountViewDto
        )
    }
}