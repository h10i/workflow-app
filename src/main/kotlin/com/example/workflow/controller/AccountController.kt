package com.example.workflow.controller

import com.example.workflow.model.AccountViewDto
import com.example.workflow.model.AccountViewResponse
import com.example.workflow.model.toViewResponse
import com.example.workflow.service.AccountService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class AccountController(
    private val accountService: AccountService,
) {
    @GetMapping("/account")
    fun get(): ResponseEntity<AccountViewResponse> {
        val accountId: UUID = accountService.getCurrentAccountId()
        val accountViewDto: AccountViewDto = accountService.getAccount(accountId)
        val accountViewResponse: AccountViewResponse = accountViewDto.toViewResponse()
        return ResponseEntity.ok().body(accountViewResponse)
    }
}
