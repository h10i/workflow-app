package com.example.workflow.e2e.feature.account

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
import kotlin.test.*

@E2ETest
class AccountApiTest(
    @Autowired
    private val restTemplate: E2ETestRestTemplate
) : AbstractE2ETest() {
    @Nested
    inner class RegisterAccount {
        @Test
        fun `POST valid request returns 201 Created`() {
            // Arrange
            val json = """
            {
              "emailAddress": "user001@example.com",
              "password": "P4sSw0rd!"
            }
            """.trimIndent()

            // Act
            val response = restTemplate.post(
                responseType = String::class.java,
                path = ApiPath.Account.BASE,
                body = json,
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
                    "emailAddress":"user001@example.com",
                    "roleNames":[]
                }
                """
            )

            assertEquals(expectedBody, actualBodyWithoutId)
        }

        @Test
        fun `POST invalid request returns 400 Bad Request when email address is already registered`() {
            // Arrange
            val json = """
            {
              "emailAddress": "test@example.com",
              "password": "P4sSw0rd!"
            }
            """.trimIndent()

            // Act
            val response = restTemplate.post(
                responseType = String::class.java,
                path = ApiPath.Account.BASE,
                body = json,
            )

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val expectedBody = mapper.readTree(
                """
                {
                    "errors": {
                        "emailAddress": [
                            "This email address is already registered."
                        ]
                    }
                }
                """
            )
            val actualBody = mapper.readTree(response.body)
            assertEquals(expectedBody, actualBody)
        }
    }

    @Nested
    inner class GetAccount {
        @Test
        fun `GET account with valid credentials returns 200 OK`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticate()

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
                accessToken = authResult.accessToken
            )

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val expectedBody = mapper.readTree(
                """
                {
                    "id":"15101d3d-0b10-4e5a-aae1-b21ccdf06b34",
                    "emailAddress":"test@example.com",
                    "roleNames":["USER","ADMIN"]
                }
                """
            )
            val actualBody = mapper.readTree(response.body)
            assertEquals(expectedBody, actualBody)
        }

        @Test
        fun `GET account with invalid credentials returns 401 Unauthorize`() {
            // Arrange

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
        }
    }
}