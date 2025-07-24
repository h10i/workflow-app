package com.example.workflow.e2e.feature.token

import com.example.workflow.common.path.ApiPath
import com.example.workflow.e2e.test.base.AbstractE2ETest
import com.example.workflow.e2e.test.util.CookieUtil
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
class TokenApiTest(
    @Autowired
    private val restTemplate: E2ETestRestTemplate
) : AbstractE2ETest() {
    @Nested
    inner class CreateToken() {
        @Test
        fun `POST token with valid credentials returns 200 OK`() {
            // Arrange
            val json = """
            {
              "emailAddress": "test@example.com",
              "password": "PASSWORD"
            }
            """.trimIndent()

            // Act
            val response = restTemplate.post(
                responseType = TokenResponse::class.java,
                path = "${ApiPath.Token.BASE}${ApiPath.Token.TOKEN}",
                body = json
            )

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body!!.accessToken)
            assertNotNull(CookieUtil.extractCookie(response.headers, "refreshToken"))
        }


        @Test
        fun `POST token with invalid credentials returns 401 Unauthorize`() {
            // Arrange
            val json = """
            {
              "emailAddress": "test@example.com",
              "password": "invalid-PASSWORD"
            }
            """.trimIndent()

            // Act
            val response = restTemplate.post(
                responseType = TokenResponse::class.java,
                path = "${ApiPath.Token.BASE}${ApiPath.Token.TOKEN}",
                body = json
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
            assertNull(CookieUtil.extractCookie(response.headers, "refreshToken"))
        }
    }
}