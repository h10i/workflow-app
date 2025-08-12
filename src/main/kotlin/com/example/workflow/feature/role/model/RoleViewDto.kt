package com.example.workflow.feature.role.model

import java.util.*

data class RoleViewDto(
    val id: UUID,
    val name: String,
)

fun RoleViewDto.toViewResponse() = RoleViewResponse(
    id = id,
    name = name,
)
