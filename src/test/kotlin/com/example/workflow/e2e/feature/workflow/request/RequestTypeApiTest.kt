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
}
