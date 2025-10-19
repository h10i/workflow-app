package com.example.workflow.feature.workflow.model.request

import com.fasterxml.jackson.databind.JsonNode
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class RequestTypeViewResponse(
    @get:Schema(
        description = "Request type id",
        example = "7a98fef2-7993-43c7-9101-45bbf743a93d",
    )
    val id: UUID,

    @get:Schema(
        description = "Request type name",
        example = "expense",
    )
    val name: String,

    @get:Schema(
        description = "Description of the request type",
        example = "use expense",
        nullable = true
    )
    val description: String?,

    @get:Schema(
        description = "JSON Schema definition for the request detail data",
        example = "{\"type\": \"object\", \"required\": [\"amount\"], \"properties\": {...}}",
    )
    val schemaDefinition: JsonNode,
)
