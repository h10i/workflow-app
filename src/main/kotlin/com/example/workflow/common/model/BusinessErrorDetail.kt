package com.example.workflow.common.model

data class BusinessErrorDetail(
    val field: String? = null,
    val rejectedValue: Any? = null,
    val code: String? = null,
    val message: String,
)
