package com.example.workflow.feature.account.controller

import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.account.model.toViewResponse
import com.example.workflow.feature.account.service.AccountService
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
    @GetMapping("/me")
    fun get(): ResponseEntity<AccountViewResponse> {
        val accountId: UUID = accountService.getCurrentAccountId()
        val accountViewDto: AccountViewDto = accountService.getAccount(accountId)
        val accountViewResponse: AccountViewResponse = accountViewDto.toViewResponse()
        return ResponseEntity.ok().body(accountViewResponse)
    }
}