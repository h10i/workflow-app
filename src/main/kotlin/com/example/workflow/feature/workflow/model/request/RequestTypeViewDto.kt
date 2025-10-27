package com.example.workflow.feature.workflow.model.request

import com.fasterxml.jackson.databind.JsonNode
import java.util.*

data class RequestTypeViewDto(
    val id: UUID,
    val name: String,
    val description: String?,
    val schemaDefinition: JsonNode,
)

fun RequestTypeViewDto.toViewResponse() = RequestTypeViewResponse(
    id = id,
    name = name,
    description = description,
    schemaDefinition = schemaDefinition,
)
