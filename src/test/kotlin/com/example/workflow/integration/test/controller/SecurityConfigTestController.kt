package com.example.workflow.integration.test.controller

import com.example.workflow.common.path.ApiPath
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
            // Role
            "${ApiPath.Role.BASE}${ApiPath.Role.PATH_PATTERN_WITH_ID}",
            ApiPath.Role.BASE,
        ]
    )
    fun get() = ResponseEntity.status(HttpStatus.OK).body("get")

    @PostMapping(
        path = [
            // Account
            ApiPath.Account.BASE,
            // Role
            ApiPath.Role.BASE,
            // Auth
            "${ApiPath.Token.BASE}${ApiPath.Token.TOKEN}",
            "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REFRESH_TOKEN}",
        ]
    )
    fun post() = ResponseEntity.status(HttpStatus.OK).body("post")

    @PatchMapping(
        path = [
            // Account
            "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
        ]
    )
    fun patch() = ResponseEntity.status(HttpStatus.OK).body("patch")

    @DeleteMapping(
        path = [
            // Account
            "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
            // Role
            "${ApiPath.Role.BASE}${ApiPath.Role.PATH_PATTERN_WITH_ID}",
            // Auth
            "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE}",
            "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE_ALL}",
        ]
    )
    fun delete() = ResponseEntity.status(HttpStatus.OK).body("delete")
}