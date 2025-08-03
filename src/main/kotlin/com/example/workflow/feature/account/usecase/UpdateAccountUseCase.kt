package com.example.workflow.feature.account.usecase

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.toViewDto
import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.UpdateAccountRequest
import com.example.workflow.feature.account.service.AccountService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UpdateAccountUseCase(
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder,
) {
    data class Result(
        val accountViewDto: AccountViewDto
    )

    fun execute(request: UpdateAccountRequest): Result {
        val accountId: UUID = accountService.getCurrentAccountId()
        val account: Account = accountService.getAccount(accountId)
        request.emailAddress?.let { newEmailAddress ->
            accountService.verifyEmailAddressAvailability(newEmailAddress)
            account.emailAddress = newEmailAddress
        }
        request.password?.let { newPassword -> account.password = passwordEncoder.encode(newPassword) }

        val savedAccount = accountService.saveAccount(account)

        return Result(
            accountViewDto = savedAccount.toViewDto()
        )
    }
}
