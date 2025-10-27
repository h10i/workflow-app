package com.example.workflow.feature.workflow.model.request

import com.fasterxml.jackson.databind.JsonNode
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateRequestTypeRequest(
    @field:NotBlank(message = "Name must not be blank")
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

    // todo: @ValidJsonSchemaDefinition
    @field:NotNull(message = "Schema definition must not be blank")
    @get:Schema(
        description = "JSON Schema definition for the request detail data",
        example = "{\"type\": \"object\", \"required\": [\"amount\"], \"properties\": {...}}",
    )
    val schemaDefinition: JsonNode,
)
