package com.example.workflow.feature.account.usecase

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.toViewDto
import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.RegisterAccountRequest
import com.example.workflow.feature.account.service.AccountService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegisterAccountUseCase(
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder,
) {
    data class Result(
        val accountViewDto: AccountViewDto
    )

    fun execute(request: RegisterAccountRequest): Result {
        accountService.verifyEmailAddressAvailability(request.emailAddress)

        val account = Account(
            emailAddress = request.emailAddress,
            password = passwordEncoder.encode(request.password),
        )

        val savedAccount = accountService.saveAccount(account)

        return Result(
            accountViewDto = savedAccount.toViewDto(),
        )
    }
}