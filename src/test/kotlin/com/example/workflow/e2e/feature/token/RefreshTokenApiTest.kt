package com.example.workflow.e2e.feature.token

import com.example.workflow.common.path.ApiPath
import com.example.workflow.e2e.test.base.AbstractE2ETest
import com.example.workflow.e2e.test.web.client.E2ETestRestTemplate
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.support.annotation.E2ETest
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
    inner class RefreshToken() {
        @Test
        fun `should return 200 OK when valid request with valid refresh token`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.registerAccountAndAuthenticate()
            val cookie = "refreshToken=${authResult.refreshToken}"

            // Act
            val response = restTemplate.post(
                responseType = TokenResponse::class.java,
                path = "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REFRESH_TOKEN}",
                body = "",
                cookie = cookie,
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
                responseType = TokenResponse::class.java,
                path = "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REFRESH_TOKEN}",
                body = "",
                cookie = cookie,
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
        }
    }

    @Nested
    inner class RevokeRefreshToken() {
        @Test
        fun `should return 204 No Content when valid request with valid credentials`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.registerAccountAndAuthenticate()
            val cookie = "refreshToken=${authResult.refreshToken}"

            // Act
            val response = restTemplate.delete(
                responseType = String::class.java,
                path = "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE}",
                accessToken = authResult.accessToken,
                cookie = cookie,
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
                path = "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE}",
                accessToken = "invalid-access-token",
                cookie = cookie,
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
        }
    }

    @Nested
    inner class RevokeAllRefreshToken() {
        @Test
        fun `should return 204 No Content when valid request with valid credentials`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.registerAccountAndAuthenticate()

            // Act
            val response = restTemplate.delete(
                responseType = String::class.java,
                path = "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE_ALL}",
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
                path = "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE_ALL}",
                accessToken = "invalid-access-token",
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
        }
    }
}