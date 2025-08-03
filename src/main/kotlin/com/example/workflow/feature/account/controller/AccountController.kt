package com.example.workflow.feature.account.controller

import com.example.workflow.common.model.UnifiedErrorResponse
import com.example.workflow.common.path.ApiPath
import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.account.model.RegisterAccountRequest
import com.example.workflow.feature.account.model.UpdateAccountRequest
import com.example.workflow.feature.account.presenter.GetAccountPresenter
import com.example.workflow.feature.account.presenter.RegisterAccountPresenter
import com.example.workflow.feature.account.presenter.UpdateAccountPresenter
import com.example.workflow.feature.account.usecase.GetAccountUseCase
import com.example.workflow.feature.account.usecase.RegisterAccountUseCase
import com.example.workflow.feature.account.usecase.UpdateAccountUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiPath.Account.BASE)
class AccountController(
    private val registerAccountUseCase: RegisterAccountUseCase,
    private val registerAccountPresenter: RegisterAccountPresenter,
    private val getAccountUseCase: GetAccountUseCase,
    private val getAccountPresenter: GetAccountPresenter,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val updateAccountPresenter: UpdateAccountPresenter,
) {
    @Operation(
        summary = "Create a new account",
        description = "Registers a new user account with the system using the provided user details.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Account Information",
            required = true,
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RegisterAccountRequest::class)
                )
            ]
        ),
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Successfully created account",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = AccountViewResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data or a general business validation error occurred. Details are provided in the 'errors' map.",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UnifiedErrorResponse::class)
                    )
                ]
            )
        ],
    )
    @PostMapping
    fun registerAccount(@Valid @RequestBody request: RegisterAccountRequest): ResponseEntity<AccountViewResponse> {
        val useCaseResult: RegisterAccountUseCase.Result = registerAccountUseCase.execute(request)
        val presenterResult: RegisterAccountPresenter.Result = registerAccountPresenter.toResponse(useCaseResult)
        return ResponseEntity.status(HttpStatus.CREATED).body(presenterResult.response)
    }

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

    @Operation(
        summary = "Update account",
        description = "Updates account with the system using the provided user details.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Account Information",
            required = true,
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RegisterAccountRequest::class)
                )
            ]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully updated account",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = AccountViewResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data or a general business validation error occurred. Details are provided in the 'errors' map.",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UnifiedErrorResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authentication credentials are missing or invalid.",
                content = [Content()]
            ),
        ],
    )
    @PatchMapping(ApiPath.Account.ME)
    fun updateAccount(@Valid @RequestBody request: UpdateAccountRequest): ResponseEntity<AccountViewResponse> {
        val useCaseResult: UpdateAccountUseCase.Result = updateAccountUseCase.execute(request)
        val presenterResult: UpdateAccountPresenter.Result = updateAccountPresenter.toResponse(useCaseResult)
        return ResponseEntity.status(HttpStatus.OK).body(presenterResult.response)
    }
}