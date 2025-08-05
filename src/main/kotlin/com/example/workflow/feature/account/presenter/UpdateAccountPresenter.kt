package com.example.workflow.feature.account.presenter

import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.account.model.toViewResponse
import com.example.workflow.feature.account.usecase.UpdateAccountUseCase
import org.springframework.stereotype.Component

@Component
class UpdateAccountPresenter {
    data class Result(
        val response: AccountViewResponse
    )

    fun toResponse(useCaseResult: UpdateAccountUseCase.Result): Result {
        return Result(
            useCaseResult.accountViewDto.toViewResponse()
        )
    }
}