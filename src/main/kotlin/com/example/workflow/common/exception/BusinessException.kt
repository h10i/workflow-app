package com.example.workflow.common.exception

import org.springframework.http.HttpStatus

open class BusinessException(
    val field: String? = null,
    val messages: List<String>,
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
) : RuntimeException(messages.joinToString(",")) {
    constructor(
        message: String,
        field: String? = null,
        httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    ) : this(field, listOf(message), httpStatus)
}