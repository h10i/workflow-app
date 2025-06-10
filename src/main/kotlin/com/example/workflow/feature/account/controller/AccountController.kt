package com.example.workflow.feature.account.controller

import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.account.model.toViewResponse
import com.example.workflow.feature.account.service.AccountService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/v1/accounts")
class AccountController(
    private val accountService: AccountService,
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
    @GetMapping("/me")
    fun get(): ResponseEntity<AccountViewResponse> {
        val accountId: UUID = accountService.getCurrentAccountId()
        val accountViewDto: AccountViewDto = accountService.getAccount(accountId)
        val accountViewResponse: AccountViewResponse = accountViewDto.toViewResponse()
        return ResponseEntity.ok().body(accountViewResponse)
    }
}