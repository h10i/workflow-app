package com.example.workflow.feature.role.model

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class RoleViewResponse(
    @get:Schema(
        description = "role id",
        example = "58196e4f-62cf-4a89-817e-d54116a78a43",
    )
    val id: UUID,

    @get:Schema(
        description = "role name",
        example = "EXAMPLE_USER",
    )
    val name: String,
)
