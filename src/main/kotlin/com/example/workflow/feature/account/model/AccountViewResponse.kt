package com.example.workflow.feature.account.model

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class AccountViewResponse(
    @get:Schema(
        description = "Your account id",
        example = "15101d3d-0b10-4e5a-aae1-b21ccdf06b34",
    )
    val id: UUID,

    @get:Schema(
        description = "Your email address",
        example = "user@example.com",
    )
    val emailAddress: String,

    @get:Schema(
        description = "Your roles",
        example = "[\"USER\"]",
    )
    val roleNames: List<String>,
)