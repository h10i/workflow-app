package com.example.workflow.e2e.feature.auth

import com.example.workflow.common.path.ApiPath
import com.example.workflow.e2e.test.base.AbstractE2ETest
import com.example.workflow.e2e.test.web.client.E2ETestRestTemplate
import com.example.workflow.e2e.test.web.model.HttpRequestOptions
import com.example.workflow.feature.auth.model.TokenResponse
import com.example.workflow.support.annotation.E2ETest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@E2ETest
class RefreshTokenApiTest : AbstractE2ETest() {
    @Autowired
    private lateinit var restTemplate: E2ETestRestTemplate

    @Nested
    inner class RefreshTokenApi {
        @Test
        fun `should return 200 OK when valid request with valid refresh token`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.registerAccountAndAuthenticate()
            val cookie = "refreshToken=${authResult.refreshToken}"

            // Act
            val response = restTemplate.post(
                responseType = TokenResponse::class.java,
                path = "${ApiPath.Auth.BASE}${ApiPath.Auth.REFRESH_TOKEN}",
                httpRequestOptions = HttpRequestOptions(
                    body = "",
                    cookie = cookie,
                )
            )

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body!!.accessToken)
        }

        @Test
        fun `should return 401 Unauthorize when valid request with invalid refresh token`() {
            // Arrange
            val cookie = "refreshToken=invalid-token-value"

            // Act
            val response = restTemplate.post(
                responseType = String::class.java,
                path = "${ApiPath.Auth.BASE}${ApiPath.Auth.REFRESH_TOKEN}",
                httpRequestOptions = HttpRequestOptions(
                    body = "",
                    cookie = cookie,
                )
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            val mapper = jacksonObjectMapper()
            val expectedBody = mapper.readTree(
                """
                    {
                      "type": "about:blank",
                      "title": "Unauthorized",
                      "status": 401,
                      "instance": "/v1/auth/refresh-token"
                    }
                """.trimIndent()
            )
            val actualBody = mapper.readTree(response.body)
            assertEquals(expectedBody, actualBody)
        }
    }

    @Nested
    inner class RevokeRefreshTokenApi {
        @Test
        fun `should return 204 No Content when valid request with valid credentials`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.registerAccountAndAuthenticate()
            val cookie = "refreshToken=${authResult.refreshToken}"

            // Act
            val response = restTemplate.delete(
                responseType = String::class.java,
                path = "${ApiPath.Auth.BASE}${ApiPath.Auth.REVOKE}",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = authResult.accessToken,
                    cookie = cookie,
                )
            )

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
            assertNull(response.body)
        }

        @Test
        fun `should return 401 Unauthorize when valid request with invalid credentials`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.registerAccountAndAuthenticate()
            val cookie = "refreshToken=${authResult.refreshToken}"

            // Act
            val response = restTemplate.delete(
                responseType = String::class.java,
                path = "${ApiPath.Auth.BASE}${ApiPath.Auth.REVOKE}",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = "invalid-access-token",
                    cookie = cookie,
                )
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
        }
    }

    @Nested
    inner class RevokeAllRefreshTokenApi {
        @Test
        fun `should return 204 No Content when valid request with valid credentials`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.registerAccountAndAuthenticate()

            // Act
            val response = restTemplate.delete(
                responseType = String::class.java,
                path = "${ApiPath.Auth.BASE}${ApiPath.Auth.REVOKE_ALL}",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = authResult.accessToken,
                )
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
                path = "${ApiPath.Auth.BASE}${ApiPath.Auth.REVOKE_ALL}",
                httpRequestOptions = HttpRequestOptions(
                    accessToken = "invalid-access-token",
                )
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
        }
    }
}
