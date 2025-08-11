package com.example.workflow.e2e.feature.role

import com.example.workflow.common.path.ApiPath
import com.example.workflow.e2e.test.base.AbstractE2ETest
import com.example.workflow.e2e.test.web.client.E2ETestRestTemplate
import com.example.workflow.support.annotation.E2ETest
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
class RoleApiTest : AbstractE2ETest() {
    @Autowired
    private lateinit var restTemplate: E2ETestRestTemplate

    @Nested
    inner class CreateRole {
        @Test
        fun `POST valid request returns 201 Created`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticateWithAdmin()

            val json = """
                {
                    "name": "EXAMPLE"
                }
            """.trimIndent()

            // Act
            val response = restTemplate.post(
                responseType = String::class.java,
                path = ApiPath.Role.BASE,
                body = json,
                accessToken = authResult.accessToken,
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

            val actualBodyWithoutId: ObjectNode = (actualBody as ObjectNode).deepCopy()
            actualBodyWithoutId.remove("id")

            val expectedBody = mapper.readTree(
                """
                {
                    "name":"EXAMPLE"
                }
                """
            )
            assertEquals(expectedBody, actualBodyWithoutId)
        }

        @Test
        fun `POST invalid request returns 400 Bad Request when role name is already created`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticateWithAdmin()

            val json = """
                {
                    "name": "ADMIN"
                }
            """.trimIndent()

            // Act
            val response = restTemplate.post(
                responseType = String::class.java,
                path = ApiPath.Role.BASE,
                body = json,
                accessToken = authResult.accessToken,
            )

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val actualBody = mapper.readTree(response.body)

            val expectedBody = mapper.readTree(
                """
                {
                    "errors": {
                        "name": [
                            "This role name is already created."
                        ]
                    }
                }
                """
            )
            assertEquals(expectedBody, actualBody)
        }
    }
}