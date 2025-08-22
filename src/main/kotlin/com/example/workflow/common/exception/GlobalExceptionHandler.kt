package com.example.workflow.common.exception

import com.example.workflow.common.model.UnifiedErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Suppress("unused")
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<UnifiedErrorResponse> {
        val errors = ex.bindingResult.fieldErrors
            .groupBy({ it.field }, { it.defaultMessage ?: "Validation error" })

        return ResponseEntity.badRequest()
            .body(UnifiedErrorResponse(errors))
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedErrors(ex: UnauthorizedException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
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
