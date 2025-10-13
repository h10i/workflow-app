package com.example.workflow.common.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import java.net.URI

object ProblemDetailWriter {
    private val mapper = ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)

    fun write(request: HttpServletRequest, response: HttpServletResponse, status: HttpStatus, detail: String) {
        val problem = ProblemDetail.forStatus(status).apply {
            this.detail = detail
            this.instance = URI.create(request.requestURI)
        }

        response.resetBuffer()
        response.status = status.value()
        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
        mapper.writeValue(response.outputStream, problem)
    }
}
