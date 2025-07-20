package com.example.workflow.e2e.feature.token

import com.example.workflow.common.path.ApiPath
import com.example.workflow.e2e.test.config.TestcontainersConfiguration
import com.example.workflow.e2e.test.web.client.E2ETestRestTemplate
import com.example.workflow.feature.token.model.TokenResponse
import com.example.workflow.support.annotation.E2ETest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@E2ETest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
@ActiveProfiles("test")
@Transactional
class RefreshTokenApiTest(
    @Autowired
    private val restTemplate: E2ETestRestTemplate
) {
    @Nested
    inner class RefreshToken() {
        @Test
        fun `POST refresh token with valid refresh token returns 200 OK`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticate()
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
        fun `POST refresh token with invalid refresh token returns 401 Unauthorize`() {
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
        fun `DELETE refresh token with valid credentials and valid refresh token returns 204 No Content`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticate()
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
        fun `DELETE refresh token with invalid credentials returns 401 Unauthorize`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticate()
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
        fun `DELETE refresh token with valid credentials returns 204 No Content`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticate()

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
        fun `DELETE refresh token with invalid credentials returns 401 Unauthorize`() {
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