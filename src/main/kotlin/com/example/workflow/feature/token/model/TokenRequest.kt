package com.example.workflow.feature.token.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class TokenRequest(
    @field:NotBlank(message = "Email address must not be blank")
    @field:Email(message = "Invalid email address format")
    @Schema(description = "your email address")
    val mailAddress: String,

    @field:NotBlank(message = "password must not be blank")
    @Schema(description = "Your account password")
    val password: String,
)