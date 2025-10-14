package com.example.workflow.unit.infra.security.web

import com.example.workflow.common.util.ProblemDetailWriter
import com.example.workflow.infra.security.web.ProblemDetailAccessDeniedHandler
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException

@UnitTest
class ProblemDetailAccessDeniedHandlerTest {
    @Nested
    inner class HandleFun {
        @BeforeEach
        fun setUp() {
            mockkObject(ProblemDetailWriter)
        }

        @AfterEach
        fun tearDown() {
            unmockkObject(ProblemDetailWriter)
        }

        @Test
        fun `should write using ProblemDetailWriter`() {
            // Arrange
            val request: HttpServletRequest = mockk(relaxed = true)
            val response: HttpServletResponse = mockk(relaxed = true)
            val accessDeniedException: AccessDeniedException = mockk()
            val message = "Forbidden access"
            every { accessDeniedException.message } returns message
            val handler = ProblemDetailAccessDeniedHandler()

            // Act
            handler.handle(
                request = request,
                response = response,
                accessDeniedException = accessDeniedException,
            )

            // Assert
            verify(exactly = 1) {
                ProblemDetailWriter.write(
                    request = request,
                    response = response,
                    status = HttpStatus.FORBIDDEN,
                    detail = message,
                )
            }
        }
    }
}
