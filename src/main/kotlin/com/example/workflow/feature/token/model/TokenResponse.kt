package com.example.workflow.feature.token.model

import io.swagger.v3.oas.annotations.media.Schema

data class TokenResponse(
    @Schema(
        description = "JWT access token",
    )
    val accessToken: String,
)