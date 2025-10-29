package com.example.workflow.e2e.feature.workflow.request

import com.example.workflow.common.path.ApiPath
import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.e2e.test.base.AbstractE2ETest
import com.example.workflow.e2e.test.web.client.E2ETestRestTemplate
import com.example.workflow.e2e.test.web.model.HttpRequestOptions
import com.example.workflow.support.annotation.E2ETest
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

@E2ETest
class RequestTypeApiTest : AbstractE2ETest() {
    @Autowired
    private lateinit var restTemplate: E2ETestRestTemplate

    @Nested
    inner class CreateRequestTypeApi {
        @Test
        @Transactional
        fun `should return 201 Created when valid request with valid credentials`() {
            // Arrange
            val authResult = restTemplate.registerAccountAndAuthenticate()

            val name = "test request type name"
            val description = "test request type name description"
            val schemaDefinition = """{"type":"object"}"""
            val json = """
                {
                "name": "$name",
                "description": "$description",
                "schemaDefinition": $schemaDefinition
                }
            """.trimIndent()

            // Act
            val response = restTemplate.post(
                responseType = String::class.java,
                path = ApiPath.RequestType.BASE,
                httpRequestOptions = HttpRequestOptions(
                    body = json,
                    accessToken = authResult.accessToken,
                )
            )

            // Assert
            assertEquals(HttpStatus.CREATED, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val actualBody = mapper.readTree(response.body)
            val actualIdNode = actualBody["id"]
            assertNotNull(actualIdNode)
            assertTrue(actualIdNode.isTextual)
            try {
                UUID.fromString(actualIdNode.asText())
            } catch (_: IllegalArgumentException) {
                fail("ID is not a valid UUID format: ${actualIdNode.asText()}")
            }

            val actualBodyWithoutId = (actualBody as ObjectNode).deepCopy()
            actualBodyWithoutId.remove("id")

            val expectedBody = mapper.readTree(
                """
                {
                "name": "$name",
                "description": "$description",
                "schemaDefinition": $schemaDefinition
                }

                """.trimIndent()
            )
            assertEquals(expectedBody, actualBodyWithoutId)
        }

        @Test
        fun `should return 400 Bad Request when invalid request with valid credentials`() {
            // Arrange
            val authResult = restTemplate.registerAccountAndAuthenticate()

            val name = ""
            val description = "test request type name description"
            val schemaDefinition = """{"type":"object"}"""
            val json = """
                {
                "name": "$name",
                "description": "$description",
                "schemaDefinition": $schemaDefinition
                }
            """.trimIndent()

            // Act
            val response = restTemplate.post(
                responseType = String::class.java,
                path = ApiPath.RequestType.BASE,
                httpRequestOptions = HttpRequestOptions(
                    body = json,
                    accessToken = authResult.accessToken,
                )
            )

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val actualBody = mapper.readTree(response.body)

            val expectedBody = mapper.readTree(
                """
                {
                    "type": "about:blank",
                    "title": "Bad Request",
                    "status": 400,
                    "detail": "Your request is not valid.",
                    "instance": "${ApiPath.RequestType.BASE}",
                    "errors": [
                        {
                            "field": "${RequestType::name.name}",
                            "rejectedValue": "$name",
                            "code": "NotBlank",
                            "message": "Name must not be blank"
                        }
                    ]
                }
                """.trimIndent()
            )
            assertEquals(expectedBody, actualBody)
        }

        @Test
        fun `should return 401 Unauthorized when valid request with invalid credentials`() {
            // Arrange
            val name = ""
            val description = "test request type name description"
            val schemaDefinition = """{"type":"object"}"""
            val json = """
                {
                "name": "$name",
                "description": "$description",
                "schemaDefinition": $schemaDefinition
                }
            """.trimIndent()

            // Act
            val response = restTemplate.post(
                responseType = String::class.java,
                path = ApiPath.RequestType.BASE,
                httpRequestOptions = HttpRequestOptions(
                    body = json,
                    accessToken = "invalid-access-token",
                )
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val actualBody = mapper.readTree(response.body)

            val expectedBody = mapper.readTree(
                """
                {
                    "type": "about:blank",
                    "title": "Unauthorized",
                    "status": 401,
                    "detail": "An error occurred while attempting to decode the Jwt: Malformed token",
                    "instance": "${ApiPath.RequestType.BASE}"
                }
                """.trimIndent()
            )
            assertEquals(expectedBody, actualBody)
        }
    }

    @Nested
    inner class GetRequestTypeApi {
        @Test
        fun `should return 200 OK when valid request with valid credentials`() {
            // Arrange
            val authResult = restTemplate.registerAccountAndAuthenticate()
            val requestTypeViewResponse = restTemplate.createRequestType(
                accessToken = authResult.accessToken,
                name = "request type name 1",
                description = "request type description 1",
                schemaDefinition = """{"type":"object"}""",
            )

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = "${ApiPath.RequestType.BASE}/${requestTypeViewResponse.id}",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = authResult.accessToken,
                )
            )

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val expectedBody = mapper.readTree(
                """
                    {
                        "id":"${requestTypeViewResponse.id}",
                        "name":"${requestTypeViewResponse.name}",
                        "description":"${requestTypeViewResponse.description}",
                        "schemaDefinition":${requestTypeViewResponse.schemaDefinition}
                    }
                """.trimIndent()
            )
            val actualBody = mapper.readTree(response.body)
            assertEquals(expectedBody, actualBody)
        }

        @Test
        fun `should return 401 Unauthorized when valid request with invalid credentials`() {
            // Arrange
            val authResult = restTemplate.registerAccountAndAuthenticate()
            val requestTypeViewResponse = restTemplate.createRequestType(
                accessToken = authResult.accessToken,
                name = "request type name 1",
                description = "request type description 1",
                schemaDefinition = """{"type":"object"}""",
            )

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = "${ApiPath.RequestType.BASE}/${requestTypeViewResponse.id}",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = "invalid-access-token",
                )
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val expectedBody = mapper.readTree(
                """
                    {
                        "type": "about:blank",
                        "title": "Unauthorized",
                        "status": 401,
                        "detail": "An error occurred while attempting to decode the Jwt: Malformed token",
                        "instance": "${ApiPath.RequestType.BASE}/${requestTypeViewResponse.id}"
                    }
                """.trimIndent()
            )
            val actualBody = mapper.readTree(response.body)
            assertEquals(expectedBody, actualBody)
        }

        @Test
        fun `should return 404 Not Found when invalid request (non-existent request type id) with valid credentials`() {
            // Arrange
            val authResult = restTemplate.registerAccountAndAuthenticate()
            val requestTypeId = UUID.randomUUID()

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = "${ApiPath.RequestType.BASE}/$requestTypeId",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = authResult.accessToken,
                )
            )

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val expectedBody = mapper.readTree(
                """
                    {
                        "type": "about:blank",
                        "title": "Not Found",
                        "status": 404,
                            "detail": "Request type not found (id: $requestTypeId)",
                        "instance": "${ApiPath.RequestType.BASE}/$requestTypeId"
                    }
                """.trimIndent()
            )
            val actualBody = mapper.readTree(response.body)
            assertEquals(expectedBody, actualBody)
        }
    }

    @Nested
    inner class GetAllRequestTypesApi {
        @Test
        @Transactional
        fun `should return 200 OK when valid request with valid credentials`() {
            // Arrange
            val authResult = restTemplate.registerAccountAndAuthenticate()
            val requestTypeViewResponseList = listOf(
                restTemplate.createRequestType(
                    accessToken = authResult.accessToken,
                    name = "request type name 1",
                    description = "request type description 1",
                    schemaDefinition = """{"type1":"object1"}""",
                ),
                restTemplate.createRequestType(
                    accessToken = authResult.accessToken,
                    name = "request type name 2",
                    description = "request type description 2",
                    schemaDefinition = """{"type2":"object2"}""",
                ),
            )

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = ApiPath.RequestType.BASE,
                httpRequestOptions = HttpRequestOptions(
                    accessToken = authResult.accessToken,
                )
            )

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val expectedBody = mapper.readTree(
                """
                    {
                        "requestTypes": [
                            {
                                "id":"${requestTypeViewResponseList[0].id}",
                                "name":"${requestTypeViewResponseList[0].name}",
                                "description":"${requestTypeViewResponseList[0].description}",
                                "schemaDefinition":${requestTypeViewResponseList[0].schemaDefinition}
                            },
                            {
                                "id":"${requestTypeViewResponseList[1].id}",
                                "name":"${requestTypeViewResponseList[1].name}",
                                "description":"${requestTypeViewResponseList[1].description}",
                                "schemaDefinition":${requestTypeViewResponseList[1].schemaDefinition}
                            }
                        ]
                    }
                """.trimIndent()
            )
            val actualBody = mapper.readTree(response.body)
            assertEquals(expectedBody, actualBody)
        }

        @Test
        @Transactional
        fun `should return 401 Unauthorized when valid request with invalid credentials`() {
            // Arrange

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = ApiPath.RequestType.BASE,
                httpRequestOptions = HttpRequestOptions(
                    accessToken = "invalid-access-token",
                )
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val expectedBody = mapper.readTree(
                """
                    {
                        "type": "about:blank",
                        "title": "Unauthorized",
                        "status": 401,
                        "detail": "An error occurred while attempting to decode the Jwt: Malformed token",
                        "instance": "${ApiPath.RequestType.BASE}"
                    }
                """.trimIndent()
            )
            val actualBody = mapper.readTree(response.body)
            assertEquals(expectedBody, actualBody)
        }
    }
}
