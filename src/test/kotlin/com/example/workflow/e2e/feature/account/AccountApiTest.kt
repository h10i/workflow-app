package com.example.workflow.e2e.feature.account

import com.example.workflow.common.path.ApiPath
import com.example.workflow.e2e.test.base.AbstractE2ETest
import com.example.workflow.e2e.test.web.client.E2ETestRestTemplate
import com.example.workflow.support.annotation.E2ETest
import com.example.workflow.support.util.TestDataFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.test.*

@E2ETest
class AccountApiTest : AbstractE2ETest() {
    @Autowired
    private lateinit var restTemplate: E2ETestRestTemplate

    @Nested
    inner class RegisterAccount {
        @Test
        fun `should return 201 Created when valid request without credentials`() {
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
        fun `should return 400 Bad Request when invalid request (email address is already registered) without credentials`() {
            // Arrange
            val emailAddress = TestDataFactory.createUniqueEmailAddress()
            val password = TestDataFactory.getValidTestPassword()
            restTemplate.registerAccount(
                emailAddress = emailAddress,
                password = password,
            )

            val json = """
            {
              "emailAddress": "$emailAddress",
              "password": "$password"
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
        fun `should return 200 OK when valid request with valid credentials`() {
            // Arrange
            val emailAddress = TestDataFactory.createUniqueEmailAddress()
            val password = TestDataFactory.getValidTestPassword()
            val registeredAccount = restTemplate.registerAccount(
                emailAddress = emailAddress,
                password = password,
            )
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticate(
                emailAddress = emailAddress,
                password = password,
            )

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
                    "id":"${registeredAccount.id}",
                    "emailAddress":"${registeredAccount.emailAddress}",
                    "roleNames":${registeredAccount.roleNames}
                }
                """
            )
            val actualBody = mapper.readTree(response.body)
            assertEquals(expectedBody, actualBody)
        }

        @Test
        fun `should return 401 Unauthorize when valid request without credentials`() {
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

    @Nested
    inner class UpdateAccount {
        @Test
        @Transactional
        fun `should return 200 OK when valid request with valid credentials`() {
            // Arrange
            val emailAddress = TestDataFactory.createUniqueEmailAddress()
            val password = TestDataFactory.getValidTestPassword()
            val accountViewResponse = restTemplate.registerAccount(
                emailAddress = emailAddress,
                password = password,
            )
            val accountId = accountViewResponse.id
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticate(
                emailAddress = emailAddress,
                password = password,
            )

            val newEmailAddress = TestDataFactory.createUniqueEmailAddress()
            val newPassword = "new-${password}"
            val json = """
            {
              "emailAddress": "$newEmailAddress",
              "password": "$newPassword"
            }
            """.trimIndent()

            // Act
            val response = restTemplate.patch(
                responseType = String::class.java,
                path = "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
                body = json,
                accessToken = authResult.accessToken,
            )

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val actualBody = mapper.readTree(response.body)

            val expectedBody = mapper.readTree(
                """
                {
                    "id":"$accountId",
                    "emailAddress":"$newEmailAddress",
                    "roleNames":[]
                }
                """
            )

            assertEquals(expectedBody, actualBody)
        }

        @Test
        fun `should return 400 Bad Request when invalid request (email address is already registered) with valid credentials`() {
            // Arrange
            val emailAddress = TestDataFactory.createUniqueEmailAddress()
            val password = TestDataFactory.getValidTestPassword()
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.registerAccountAndAuthenticate(
                emailAddress = emailAddress,
                password = password,
            )

            val newPassword = "new-${password}"
            val json = """
            {
              "emailAddress": "$emailAddress",
              "password": "$newPassword"
            }
            """.trimIndent()

            // Act
            val response = restTemplate.patch(
                responseType = String::class.java,
                path = "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
                body = json,
                accessToken = authResult.accessToken,
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

        @Test
        fun `should return 401 Unauthorize when valid request with invalid credentials`() {
            // Arrange
            val emailAddress = TestDataFactory.createUniqueEmailAddress()
            val password = TestDataFactory.getValidTestPassword()
            restTemplate.registerAccountAndAuthenticate(
                emailAddress = emailAddress,
                password = password,
            )

            val newEmailAddress = TestDataFactory.createUniqueEmailAddress()
            val newPassword = "new-${password}"
            val json = """
            {
              "emailAddress": "$newEmailAddress",
              "password": "$newPassword"
            }
            """.trimIndent()

            // Act
            val response = restTemplate.patch(
                responseType = String::class.java,
                path = "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
                body = json,
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
        }
    }

    @Nested
    inner class DeleteAccount() {
        @Test
        fun `should return 204 No Content when valid request with valid credentials`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.registerAccountAndAuthenticate()

            // Act
            val response = restTemplate.delete(
                responseType = String::class.java,
                path = "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
                accessToken = authResult.accessToken,
            )

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
            assertNull(response.body)
        }


        @Test
        fun `should return 401 Unauthorize when valid request with invalid credentials`() {
            // Arrange

            // Act
            val response = restTemplate.delete(
                responseType = String::class.java,
                path = "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
                accessToken = "invalid-access-token",
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
        }
    }
}