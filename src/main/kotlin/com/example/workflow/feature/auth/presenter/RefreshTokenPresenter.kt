package com.example.workflow.feature.auth.presenter

import com.example.workflow.feature.auth.model.TokenResponse
import com.example.workflow.feature.auth.usecase.RefreshTokenUseCase
import org.springframework.stereotype.Component

@Component
class RefreshTokenPresenter {
    data class Result(
        val response: TokenResponse,
    )

    fun toResponse(useCaseResult: RefreshTokenUseCase.Result): Result {
        return Result(
            response = TokenResponse(useCaseResult.accessToken)
        )
    }
}
