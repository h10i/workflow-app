package com.example.workflow.infra.security.web

import com.example.workflow.common.util.ProblemDetailWriter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
@Suppress("unused")
class ProblemDetailAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        ProblemDetailWriter.write(
            request = request,
            response = response,
            status = HttpStatus.UNAUTHORIZED,
            detail = authException.message ?: "Unauthorized error occurred.",
        )
    }
}
