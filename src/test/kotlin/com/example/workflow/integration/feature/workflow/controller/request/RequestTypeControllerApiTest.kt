package com.example.workflow.integration.feature.workflow.controller.request

import com.example.workflow.common.path.ApiPath
import com.example.workflow.feature.workflow.controller.request.RequestTypeController
import com.example.workflow.feature.workflow.model.request.CreateRequestTypeRequest
import com.example.workflow.feature.workflow.model.request.RequestTypeViewResponse
import com.example.workflow.feature.workflow.presenter.request.RequestTypePresenter
import com.example.workflow.feature.workflow.usecase.request.CreateRequestTypeUseCase
import com.example.workflow.integration.test.config.NoSecurityConfig
import com.example.workflow.support.annotation.IntegrationTest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import java.util.*

@IntegrationTest
@WebMvcTest(RequestTypeController::class)
@Import(RequestTypeControllerApiTest.MockConfig::class, NoSecurityConfig::class)
class RequestTypeControllerApiTest {
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    @Autowired
    private lateinit var requestTypePresenter: RequestTypePresenter

    @Autowired
    private lateinit var createRequestTypeUseCase: CreateRequestTypeUseCase

    @TestConfiguration
    @Suppress("unused")
    class MockConfig {
        @Bean
        fun requestTypePresenter(): RequestTypePresenter = mockk()

        @Bean
        fun createRequestTypeUseCase(): CreateRequestTypeUseCase = mockk()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    inner class CreateRequestTypeApi {
        @Test
        fun `should return the request type information when valid request`() {
            // Arrange
            val name = "test request type name"
            val description = "test request type name description"
            val schemaDefinition = """{"type":"object"}"""

            val useCaseResult: CreateRequestTypeUseCase.Result = mockk(relaxed = true)
            val requestTypeId = UUID.randomUUID()
            val requestTypeViewResponse = RequestTypeViewResponse(
                id = requestTypeId,
                name = name,
                description = description,
                schemaDefinition = jacksonObjectMapper().readTree(schemaDefinition)
            )
            val presenterResult = RequestTypePresenter.Result(
                response = requestTypeViewResponse,
            )

            every { createRequestTypeUseCase.execute(any()) } returns useCaseResult
            every { requestTypePresenter.toResponse(useCaseResult.requestTypeViewDto) } returns presenterResult

            // Act
            val testResult = mockMvcTester
                .post()
                .uri(ApiPath.RequestType.BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                        "name": "$name",
                        "description": "$description",
                        "schemaDefinition": $schemaDefinition
                        }
                    """.trimIndent()
                )
                .exchange()

            // Assert
            val request = slot<CreateRequestTypeRequest>()
            verify(exactly = 1) { createRequestTypeUseCase.execute(capture(request)) }
            val capturedRequest = request.captured
            assertEquals(name, capturedRequest.name)
            assertEquals(description, capturedRequest.description)
            assertEquals(schemaDefinition, capturedRequest.schemaDefinition.toString())

            assertThat(testResult)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .isLenientlyEqualTo(
                    """
                        {
                        "id": "$requestTypeId",
                        "name": "$name",
                        "schemaDefinition": $schemaDefinition
                        }
                    """.trimIndent()
                )
        }

        @Test
        fun `should return the request type information when invalid request`() {
            // Arrange
            val name = ""
            val description = "test request type name description"
            val schemaDefinition = """{"type":"object"}"""

            // Act
            val testResult = mockMvcTester
                .post()
                .uri(ApiPath.RequestType.BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                        "name": "$name",
                        "description": "$description",
                        "schemaDefinition": $schemaDefinition
                        }
                    """.trimIndent()
                )
                .exchange()

            // Assert
            assertThat(testResult)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .isLenientlyEqualTo(
                    """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Your request is not valid.",
                      "instance": "/v1/request-types",
                      "errors": [
                        {
                          "field": "name",
                          "rejectedValue": "",
                          "code": "NotBlank",
                          "message": "Name must not be blank"
                        }
                      ]
                    }
                    """.trimIndent()
                )
        }
    }
}
