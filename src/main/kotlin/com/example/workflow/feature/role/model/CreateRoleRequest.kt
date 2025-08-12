package com.example.workflow.feature.role.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class CreateRoleRequest(
    @field:NotBlank(message = "Name must not be blank")
    @get:Schema(
        description = "new role name",
        example = "USER",
    )
    val name: String,
)
