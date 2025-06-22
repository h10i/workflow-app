package com.example.workflow.feature.token.presenter

import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.usecase.RefreshTokenUseCase
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