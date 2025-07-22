package com.example.workflow.feature.account.model

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class AccountViewResponse(
    @Schema(
        description = "Your account id",
        example = "15101d3d-0b10-4e5a-aae1-b21ccdf06b34",
    )
    val id: UUID,

    @Schema(
        description = "Your email address",
        example = "user@example.com",
    )
    val emailAddress: String,

    @Schema(
        description = "Your roles",
        example = "[\"USER\"]",
    )
    val roleNames: List<String>,
)