package com.example.workflow.feature.account.presenter

import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.account.model.toViewResponse
import org.springframework.stereotype.Component

@Component
class AccountPresenter {
    data class Result<T>(
        val response: T
    )

    fun toResponse(accountViewDto: AccountViewDto): Result<AccountViewResponse> {
        return Result(
            accountViewDto.toViewResponse()
        )
    }
}
