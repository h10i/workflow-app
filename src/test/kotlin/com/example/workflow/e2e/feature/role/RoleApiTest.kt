package com.example.workflow.e2e.feature.role

import com.example.workflow.common.path.ApiPath
import com.example.workflow.e2e.test.base.AbstractE2ETest
import com.example.workflow.e2e.test.web.client.E2ETestRestTemplate
import com.example.workflow.e2e.test.web.model.HttpRequestOptions
import com.example.workflow.support.annotation.E2ETest
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

@E2ETest
class RoleApiTest : AbstractE2ETest() {
    @Autowired
    private lateinit var restTemplate: E2ETestRestTemplate

    @Nested
    inner class CreateRoleApi {
        @Test
        @Transactional
        fun `should return 201 Created when valid request with ADMIN credentials`() {
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
        fun `should return 400 Bad Request when invalid request with ADMIN credentials`() {
            // invalid request: role name is already created
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

        @Test
        fun `should return 401 Unauthorized when valid request with invalid credentials`() {
            // Arrange

            // Act
            val json = """
                {
                    "name": "NEW_ADMIN"
                }
            """.trimIndent()

            // Act
            val response = restTemplate.post(
                responseType = String::class.java,
                path = ApiPath.Role.BASE,
                httpRequestOptions = HttpRequestOptions(
                    body = json,
                    accessToken = "invalid-access-token",
                )
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
        }

        @Test
        fun `should return 403 Forbidden when valid request with non-ADMIN credentials`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.registerAccountAndAuthenticate()

            // Act
            val json = """
                {
                    "name": "ADMIN"
                }
            """.trimIndent()

            // Act
            val response = restTemplate.post(
                responseType = String::class.java,
                path = ApiPath.Role.BASE,
                httpRequestOptions = HttpRequestOptions(
                    body = json,
                    accessToken = authResult.accessToken,
                )
            )

            // Assert
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
            assertNull(response.body)
        }
    }

    @Nested
    inner class GetRoleApi {
        @Test
        fun `should return 200 OK when valid request with ADMIN credentials`() {
            // Arrange
            val authResultWithAdmin: E2ETestRestTemplate.AuthResult = restTemplate.authenticateWithAdmin()
            val roleViewResponse =
                restTemplate.createRole(name = "ROLE_TO_GET_200", accessToken = authResultWithAdmin.accessToken)

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = "${ApiPath.Role.BASE}/${roleViewResponse.id}",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = authResultWithAdmin.accessToken,
                )

            )

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val expectedBody = mapper.readTree(
                """
                    {
                        "id":"${roleViewResponse.id}",
                        "name":"${roleViewResponse.name}"
                    }
                """.trimIndent()
            )
            val actualBody = mapper.readTree(response.body)
            assertEquals(expectedBody, actualBody)
        }

        @Test
        fun `should return 401 Unauthorized when valid request with invalid credentials`() {
            // Arrange
            val authResultWithAdmin: E2ETestRestTemplate.AuthResult = restTemplate.authenticateWithAdmin()
            val roleViewResponse =
                restTemplate.createRole(name = "ROLE_TO_GET_401", accessToken = authResultWithAdmin.accessToken)

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = "${ApiPath.Role.BASE}/${roleViewResponse.id}",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = "invalid-access-token",
                )
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
        }

        @Test
        fun `should return 403 Forbidden when valid request with non-ADMIN credentials`() {
            // Arrange
            val authResultWithAdmin: E2ETestRestTemplate.AuthResult = restTemplate.authenticateWithAdmin()
            val roleViewResponse =
                restTemplate.createRole(name = "ROLE_TO_GET_403", accessToken = authResultWithAdmin.accessToken)
            val authResultWithUser: E2ETestRestTemplate.AuthResult = restTemplate.registerAccountAndAuthenticate()

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = "${ApiPath.Role.BASE}/${roleViewResponse.id}",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = authResultWithUser.accessToken,
                )
            )

            // Assert
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
            assertNull(response.body)
        }

        @Test
        fun `should return 404 Not Found when invalid request (non-existent role id) with ADMIN credentials`() {
            // Arrange
            val authResultWithAdmin: E2ETestRestTemplate.AuthResult = restTemplate.authenticateWithAdmin()
            restTemplate.createRole(name = "ROLE_TO_GET_403", accessToken = authResultWithAdmin.accessToken)
            val roleId = UUID.randomUUID()

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = "${ApiPath.Role.BASE}/$roleId",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = authResultWithAdmin.accessToken,
                )
            )

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
            assertNotNull(response.body)
            val mapper = jacksonObjectMapper()
            val expectedBody = mapper.readTree(
                """
                    {
                        "errors":{
                            "general": [
                                "Role not found with criteria: id: $roleId"
                            ]
                        }
                    }
                """.trimIndent()
            )
            val actualBody = mapper.readTree(response.body)
            assertEquals(expectedBody, actualBody)
        }
    }

    @Nested
    inner class GetAllRolesApi {
        @Test
        fun `should return 200 OK when valid request with ADMIN credentials`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticateWithAdmin()

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = ApiPath.Role.BASE,
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
                        "roles":[
                            {
                                "id":"7189ff37-21e6-4ee5-a8c5-83282e3e1de1",
                                "name":"USER"
                            },
                            {
                                "id":"3c21a2e0-7594-45bf-8eb3-6bf9edbed13d",
                                "name":"ADMIN"
                            }
                        ]
                    }
                """.trimIndent()
            )
            val actualBody = mapper.readTree(response.body)
            assertEquals(expectedBody, actualBody)
        }

        @Test
        fun `should return 401 Unauthorized when valid request with invalid credentials`() {
            // Arrange

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = ApiPath.Role.BASE,
                httpRequestOptions = HttpRequestOptions(
                    accessToken = "invalid-access-token",
                )
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
        }

        @Test
        fun `should return 403 Forbidden when valid request with non-ADMIN credentials`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.registerAccountAndAuthenticate()

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = ApiPath.Role.BASE,
                httpRequestOptions = HttpRequestOptions(
                    accessToken = authResult.accessToken,
                )
            )

            // Assert
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
            assertNull(response.body)
        }
    }

    @Nested
    inner class DeleteRoleApi {
        @Test
        fun `should return 204 No Content when valid request with ADMIN credentials`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticateWithAdmin()
            val roleViewResponse =
                restTemplate.createRole(name = "ROLE_TO_DELETE_204", accessToken = authResult.accessToken)

            // Act
            val response = restTemplate.delete(
                responseType = String::class.java,
                path = "${ApiPath.Role.BASE}/${roleViewResponse.id}",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = authResult.accessToken,
                )
            )

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
            assertNull(response.body)
        }

        @Test
        fun `should return 401 Unauthorized when valid request with invalid credentials`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticateWithAdmin()
            val roleViewResponse =
                restTemplate.createRole(name = "ROLE_TO_DELETE_401", accessToken = authResult.accessToken)

            // Act
            val response = restTemplate.delete(
                responseType = String::class.java,
                path = "${ApiPath.Role.BASE}/${roleViewResponse.id}",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = "invalid-access-token",
                )
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
        }

        @Test
        fun `should return 403 Forbidden when valid request with non-ADMIN credentials`() {
            // Arrange
            val authResultWithAdmin: E2ETestRestTemplate.AuthResult = restTemplate.authenticateWithAdmin()
            val roleViewResponse =
                restTemplate.createRole(name = "ROLE_TO_DELETE_403", accessToken = authResultWithAdmin.accessToken)
            val authResultWithUser: E2ETestRestTemplate.AuthResult = restTemplate.registerAccountAndAuthenticate()

            // Act
            val response = restTemplate.delete(
                responseType = String::class.java,
                path = "${ApiPath.Role.BASE}/${roleViewResponse.id}",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = authResultWithUser.accessToken,
                )
            )

            // Assert
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
            assertNull(response.body)
        }

        @Test
        fun `should return 404 Not Found when invalid request (non-existent role id) with ADMIN credentials`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticateWithAdmin()
            val roleId = UUID.randomUUID()

            // Act
            val response = restTemplate.delete(
                responseType = String::class.java,
                path = "${ApiPath.Role.BASE}/$roleId",
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
                        "errors":{
                            "general": [
                                "Role not found with criteria: id: $roleId"
                            ]
                        }
                    }
                """.trimIndent()
            )
            val actualBody = mapper.readTree(response.body)
            assertEquals(expectedBody, actualBody)
        }
    }
}
