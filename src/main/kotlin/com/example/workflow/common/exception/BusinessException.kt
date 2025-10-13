package com.example.workflow.common.exception

import com.example.workflow.common.model.BusinessErrorDetail
import org.springframework.http.HttpStatus

open class BusinessException(
    val errors: List<BusinessErrorDetail>,
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    override val message: String? = null,
) : RuntimeException(message ?: errors.joinToString { it.message }) {
    constructor(
        field: String? = null,
        rejectedValue: Any? = null,
        code: String? = null,
        message: String,
        httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    ) : this(
        errors = listOf(
            BusinessErrorDetail(
                field = field,
                rejectedValue = rejectedValue,
                code = code,
                message = message,
            )
        ),
        httpStatus = httpStatus,
        message = message,
    )
}
