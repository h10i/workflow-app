package com.example.workflow.feature.account.presenter

import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.account.model.toViewResponse
import com.example.workflow.feature.account.usecase.RegisterAccountUseCase
import org.springframework.stereotype.Component

@Component
class RegisterAccountPresenter {
    data class Result(
        val response: AccountViewResponse
    )

    fun toResponse(useCaseResult: RegisterAccountUseCase.Result): Result {
        return Result(
            useCaseResult.accountViewDto.toViewResponse()
        )
    }
}