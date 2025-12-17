package com.example.workflow.feature.workflow.model.request

import io.swagger.v3.oas.annotations.media.Schema

data class UpdateRequestTypeRequest(
    @get:Schema(
        description = "Request type name",
        example = "expense",
    )
    val name: String? = null,

    @get:Schema(
        description = "Description of the request type",
        example = "use expense",
        nullable = true
    )
    val description: String? = null,
)
