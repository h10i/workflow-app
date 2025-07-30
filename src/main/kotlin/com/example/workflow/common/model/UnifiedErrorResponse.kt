package com.example.workflow.common.model

data class UnifiedErrorResponse(
    val errors: Map<String, List<String>>
)