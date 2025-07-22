package com.example.workflow.feature.token.presenter

import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.feature.token.usecase.IssueTokenUseCase
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class TokenPresenter {
    data class Result(
        val response: TokenResponse,
        val refreshTokenCookie: ResponseCookie,
    )

    fun toResponse(useCaseResult: IssueTokenUseCase.Result): Result {
        return Result(
            response = TokenResponse(useCaseResult.accessToken),
            refreshTokenCookie = useCaseResult.refreshTokenCookie,
        )
    }
}