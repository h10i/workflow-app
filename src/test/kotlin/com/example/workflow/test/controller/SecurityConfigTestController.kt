package com.example.workflow.test.controller

import com.example.workflow.common.path.ApiPath
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SecurityConfigTestController {
    @GetMapping(
        path = [
            // SpringDoc
            ApiPath.SpringDoc.API_DOCS_ALL,
            ApiPath.SpringDoc.SWAGGER_UI_HTML,
            ApiPath.SpringDoc.SWAGGER_UI_ALL,
            // Auth
            "${ApiPath.Token.BASE}${ApiPath.Token.TOKEN}",
            "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REFRESH_TOKEN}",
        ]

    )
    fun permitAllPaths() = ResponseEntity.ok("permitAll")

    @GetMapping(
        path = [
            // auth
            "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE}",
            "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE_ALL}",
            // account
            "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
        ]
    )
    fun authenticatedPaths() = ResponseEntity.ok("authenticated")
}