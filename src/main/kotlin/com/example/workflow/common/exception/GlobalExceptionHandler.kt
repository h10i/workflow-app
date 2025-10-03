package com.example.workflow.common.exception

import com.example.workflow.common.model.UnifiedErrorResponse
import com.example.workflow.common.model.ValidationErrorDetail
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Suppress("unused")
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ProblemDetail {
        val errors = ex.bindingResult.fieldErrors.map {
            ValidationErrorDetail(
                field = it.field,
                rejectedValue = it.rejectedValue,
                code = it.code,
                message = it.defaultMessage,
            )
        }
        return ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply { setProperty("errors", errors) }
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedErrors(ex: UnauthorizedException): ProblemDetail {
        return ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleBusinessErrors(ex: ResourceNotFoundException): ResponseEntity<UnifiedErrorResponse> {
        val errorsMap = mapOf("general" to listOf(ex.message ?: "Resource not found."))
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(UnifiedErrorResponse(errorsMap))
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessErrors(ex: BusinessException): ResponseEntity<UnifiedErrorResponse> {
        val errorKey = ex.field ?: "general"
        val errorsMap = mapOf(errorKey to ex.messages)
        return ResponseEntity
            .status(ex.httpStatus)
            .body(UnifiedErrorResponse(errorsMap))
    }
}
