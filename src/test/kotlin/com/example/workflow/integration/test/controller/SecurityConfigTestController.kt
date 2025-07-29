package com.example.workflow.integration.test.controller

import com.example.workflow.common.path.ApiPath
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Profile("security-test-controller")
@RestController
class SecurityConfigTestController {
    @GetMapping(
        path = [
            // SpringDoc
            ApiPath.SpringDoc.API_DOCS_ALL,
            ApiPath.SpringDoc.SWAGGER_UI_HTML,
            ApiPath.SpringDoc.SWAGGER_UI_ALL,
            // Account
            "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
        ]
    )
    fun get() = ResponseEntity.ok("get")

    @PostMapping(
        path = [
            // Account
            ApiPath.Account.BASE,
            // Auth
            "${ApiPath.Token.BASE}${ApiPath.Token.TOKEN}",
            "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REFRESH_TOKEN}",
        ]
    )
    fun post() = ResponseEntity.ok("post")

    @DeleteMapping(
        path = [
            // Auth
            "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE}",
            "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE_ALL}",
        ]
    )
    fun delete() = ResponseEntity.ok("delete")
}