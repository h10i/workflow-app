package com.example.workflow.common.model

data class ValidationErrorDetail(
    val field: String,
    val rejectedValue: Any?,
    val code: String?,
    val message: String?,
)
