package com.example.workflow.feature.account.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email

data class UpdateAccountRequest(
    @field:Email(message = "Invalid email address format")
    @get:Schema(
        description = "your email address",
        example = "user@example.com",
        required = false,
    )
    val emailAddress: String? = null,

    @get:Schema(
        description = "Your account password",
        example = "P4sSw0rd!",
        required = false,
    )
    val password: String? = null,
)
