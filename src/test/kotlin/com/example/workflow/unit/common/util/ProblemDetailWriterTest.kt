package com.example.workflow.unit.common.util

import com.example.workflow.common.util.ProblemDetailWriter
import com.example.workflow.support.annotation.UnitTest
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.WriteListener
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import java.io.ByteArrayOutputStream
import java.net.URI

@UnitTest
class ProblemDetailWriterTest {
    @Nested
    inner class WriteFun {
        private val mapper = ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)

        @Test
        fun `should set response to ProblemDetail`() {
            // Arrange
            val requestURI = "/v1/test"
            val request: HttpServletRequest = mockk()
            every { request.requestURI } returns requestURI
            val outputStream = ByteArrayOutputStream()
            val servletOutputStream = object : ServletOutputStream() {
                override fun write(b: Int) {
                    outputStream.write(b)
                }

                override fun isReady(): Boolean = true
                override fun setWriteListener(p0: WriteListener?) = Unit
            }
            val response: HttpServletResponse = mockk(relaxed = true)
            every { response.outputStream } returns servletOutputStream
            val status: HttpStatus = HttpStatus.UNAUTHORIZED
            val detail = "authentication failed."

            // Act
            ProblemDetailWriter.write(
                request = request,
                response = response,
                status = status,
                detail = detail,
            )

            // Assert
            verify { response.status = status.value() }
            verify { response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE }

            val json = outputStream.toString(Charsets.UTF_8)
            val problem = mapper.readValue(json, ProblemDetail::class.java)

            assertEquals(status.value(), problem.status)
            assertEquals(detail, problem.detail)
            assertEquals(URI.create(requestURI), problem.instance)
            assertEquals("about:blank", problem.type.toString())
            assertEquals("Unauthorized", problem.title)
        }
    }
}
