package com.example.workflow.feature.account.controller

import com.example.workflow.common.path.ApiPath
import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.account.presenter.GetAccountPresenter
import com.example.workflow.feature.account.usecase.GetAccountUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiPath.Account.BASE)
class AccountController(
    private val getAccountUseCase: GetAccountUseCase,
    private val getAccountPresenter: GetAccountPresenter,
) {
    @Operation(
        summary = "Get your account information",
        description = "Retrieves the registered account information for the authenticated user. A valid JWT token is required in the Authorization header.",
        security = [SecurityRequirement(name = "bearer-key")],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved account information",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = AccountViewResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authentication credentials are missing or invalid.",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Account not found.",
                content = [Content()]
            )
        ],
    )
    @GetMapping(ApiPath.Account.ME)
    fun get(): ResponseEntity<AccountViewResponse> {
        val useCaseResult: GetAccountUseCase.Result = getAccountUseCase.execute()
        val presenterResult: GetAccountPresenter.Result = getAccountPresenter.toResponse(useCaseResult)
        return ResponseEntity.ok().body(presenterResult.response)
    }
}