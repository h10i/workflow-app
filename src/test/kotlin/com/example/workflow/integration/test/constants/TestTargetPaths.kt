package com.example.workflow.integration.test.constants

object TestTargetPaths {
    val permitAllPaths = setOf(
        // SpringDoc
        "/v3/api-docs/**",
        "/swagger-ui.html",
        "/swagger-ui/**",
        // Auth
        "/v1/auth/token",
        "/v1/auth/refresh-token",
    )
    val authenticatedPaths = setOf(
        // Auth
        "/v1/auth/revoke",
        "/v1/auth/revoke/all",
        // Account
        "/v1/accounts/me",
    )
}