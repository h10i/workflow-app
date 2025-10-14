package com.example.workflow.infra.security.web

import com.example.workflow.common.util.ProblemDetailWriter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
@Suppress("unused")
class ProblemDetailAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        ProblemDetailWriter.write(
            request = request,
            response = response,
            status = HttpStatus.FORBIDDEN,
            detail = accessDeniedException.message ?: "Access denied.",
        )
    }
}
