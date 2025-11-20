package com.example.workflow.integration.feature.workflow.controller.request

import com.example.workflow.common.path.ApiPath
import com.example.workflow.feature.workflow.controller.request.RequestTypeController
import com.example.workflow.feature.workflow.model.request.CreateRequestTypeRequest
import com.example.workflow.feature.workflow.model.request.RequestTypeViewListResponse
import com.example.workflow.feature.workflow.model.request.RequestTypeViewResponse
import com.example.workflow.feature.workflow.presenter.request.RequestTypePresenter
import com.example.workflow.feature.workflow.usecase.request.CreateRequestTypeUseCase
import com.example.workflow.feature.workflow.usecase.request.DeleteRequestTypeUseCase
import com.example.workflow.feature.workflow.usecase.request.GetAllRequestTypesUseCase
import com.example.workflow.feature.workflow.usecase.request.GetRequestTypeUseCase
import com.example.workflow.integration.test.config.NoSecurityConfig
import com.example.workflow.support.annotation.IntegrationTest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
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
import org.springframework.test.web.servlet.assertj.MvcTestResult
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

    @Autowired
    private lateinit var getRequestTypeUseCase: GetRequestTypeUseCase

    @Autowired
    private lateinit var getAllRequestTypeUseCase: GetAllRequestTypesUseCase

    @Autowired
    private lateinit var deleteRequestTypeUseCase: DeleteRequestTypeUseCase

    @TestConfiguration
    @Suppress("unused")
    class MockConfig {
        @Bean
        fun requestTypePresenter(): RequestTypePresenter = mockk()

        @Bean
        fun createRequestTypeUseCase(): CreateRequestTypeUseCase = mockk()

        @Bean
        fun getRequestTypeUseCase(): GetRequestTypeUseCase = mockk()

        @Bean
        fun getAllRequestTypeUseCase(): GetAllRequestTypesUseCase = mockk()

        @Bean
        fun deleteRequestTypeUseCase(): DeleteRequestTypeUseCase = mockk()
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

    @Nested
    inner class GetRequestTypeApi {
        @Test
        fun `should return the request type information when valid request`() {
            // Arrange
            val requestTypeId = UUID.randomUUID()
            val useCaseResult: GetRequestTypeUseCase.Result = mockk()
            val presenterResult = RequestTypePresenter.Result(
                response = RequestTypeViewResponse(
                    id = requestTypeId,
                    name = "request type name",
                    description = "request type description",
                    schemaDefinition = jacksonObjectMapper().readTree("""{"type":"object"}"""),
                )
            )

            every { getRequestTypeUseCase.execute(requestTypeId) } returns useCaseResult
            every { requestTypePresenter.toResponse(useCaseResult.requestTypeViewDto) } returns presenterResult

            // Act
            val testResult = mockMvcTester
                .get()
                .uri("${ApiPath.RequestType.BASE}/$requestTypeId")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

            // Assert
            assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .isLenientlyEqualTo(
                    """
                        {
                            "id": "${presenterResult.response.id}",
                            "name": "${presenterResult.response.name}",
                            "description": "${presenterResult.response.description}",
                            "schemaDefinition": ${presenterResult.response.schemaDefinition}
                        }
                    """.trimIndent()
                )
        }
    }

    @Nested
    inner class GetAllRequestTypesApi {
        @Test
        fun `should return the request type information when valid request`() {
            // Arrange
            val requestTypeId = UUID.randomUUID()
            val useCaseResult: GetAllRequestTypesUseCase.Result = mockk()
            val presenterResult = RequestTypePresenter.Result(
                response = RequestTypeViewListResponse(
                    requestTypes = listOf(
                        RequestTypeViewResponse(
                            id = requestTypeId,
                            name = "request type name 1",
                            description = "request type description 1",
                            schemaDefinition = jacksonObjectMapper().readTree("""{"type1":"object1"}"""),
                        ),
                        RequestTypeViewResponse(
                            id = requestTypeId,
                            name = "request type name 2",
                            description = "request type description 2",
                            schemaDefinition = jacksonObjectMapper().readTree("""{"type2":"object2"}"""),
                        ),
                    )
                )
            )

            every { getAllRequestTypeUseCase.execute() } returns useCaseResult
            every { requestTypePresenter.toResponse(useCaseResult.requestTypeViewDtoList) } returns presenterResult

            // Act
            val testResult = mockMvcTester
                .get()
                .uri(ApiPath.RequestType.BASE)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

            // Assert
            assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .isLenientlyEqualTo(
                    """
                        {
                            "requestTypes": [
                                {
                                    "id": "${presenterResult.response.requestTypes[0].id}",
                                    "name": "${presenterResult.response.requestTypes[0].name}",
                                    "description": "${presenterResult.response.requestTypes[0].description}",
                                    "schemaDefinition": ${presenterResult.response.requestTypes[0].schemaDefinition}
                                },
                                {
                                    "id": "${presenterResult.response.requestTypes[1].id}",
                                    "name": "${presenterResult.response.requestTypes[1].name}",
                                    "description": "${presenterResult.response.requestTypes[1].description}",
                                    "schemaDefinition": ${presenterResult.response.requestTypes[1].schemaDefinition}
                                }
                            ]
                        }
                    """.trimIndent()
                )
        }
    }

    @Nested
    inner class DeleteRequestTypeApi {
        @Test
        fun `should delete a request type and return no content when valid request`() {
            // Arrange
            val requestTypeId = UUID.randomUUID()
            every { deleteRequestTypeUseCase.execute(requestTypeId) } just runs

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .delete()
                .uri("${ApiPath.RequestType.BASE}/$requestTypeId")
                .exchange()

            // Assert
            assertThat(testResult).hasStatus(HttpStatus.NO_CONTENT)
        }
    }
}
