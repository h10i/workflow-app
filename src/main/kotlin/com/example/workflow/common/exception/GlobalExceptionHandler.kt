package com.example.workflow.common.exception

import com.example.workflow.common.model.ValidationErrorDetail
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.core.AuthenticationException
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
        return ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
            detail = "Your request is not valid."
            setProperty("errors", errors)
        }
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedErrors(ex: UnauthorizedException): ProblemDetail {
        return ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ProblemDetail {
        return ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED).apply {
            detail = ex.message ?: "Authentication error occurred."
        }
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleBusinessErrors(ex: ResourceNotFoundException): ProblemDetail {
        return ProblemDetail.forStatus(HttpStatus.NOT_FOUND).apply {
            detail = ex.message ?: "Resource not found."
        }
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessErrors(ex: BusinessException): ProblemDetail {
        return ProblemDetail.forStatus(ex.httpStatus).apply {
            setProperty("errors", ex.errors)
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedErrors(ex: Exception): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        problem.detail = ex.message ?: "Unexpected error occurred."
        return problem
    }
}
