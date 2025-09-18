package com.example.workflow.e2e.feature.auth

import com.example.workflow.common.path.ApiPath
import com.example.workflow.e2e.test.base.AbstractE2ETest
import com.example.workflow.e2e.test.util.CookieUtil
import com.example.workflow.e2e.test.web.client.E2ETestRestTemplate
import com.example.workflow.e2e.test.web.model.HttpRequestOptions
import com.example.workflow.feature.auth.model.TokenResponse
import com.example.workflow.support.annotation.E2ETest
import com.example.workflow.support.util.TestDataFactory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@E2ETest
class TokenApiTest : AbstractE2ETest() {
    @Autowired
    private lateinit var restTemplate: E2ETestRestTemplate

    @Nested
    inner class CreateTokenApi {
        @Test
        fun `should return 200 OK when valid request`() {
            // Arrange
            val emailAddress = TestDataFactory.createUniqueEmailAddress()
            val password = TestDataFactory.createValidTestPassword()
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
                responseType = TokenResponse::class.java,
                path = "${ApiPath.Auth.BASE}${ApiPath.Auth.TOKEN}",
                httpRequestOptions = HttpRequestOptions(
                    body = json
                )
            )

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body!!.accessToken)
            assertNotNull(CookieUtil.extractCookie(response.headers, "refreshToken"))
        }

        @Test
        fun `should return 401 Unauthorize when invalid request`() {
            // Arrange
            val emailAddress = TestDataFactory.createUniqueEmailAddress()
            val password = TestDataFactory.createValidTestPassword()
            restTemplate.registerAccount(
                emailAddress = emailAddress,
                password = password,
            )
            val json = """
            {
              "emailAddress": "$emailAddress",
              "password": "invalid-$password"
            }
            """.trimIndent()

            // Act
            val response = restTemplate.post(
                responseType = TokenResponse::class.java,
                path = "${ApiPath.Auth.BASE}${ApiPath.Auth.TOKEN}",
                httpRequestOptions = HttpRequestOptions(
                    body = json
                )
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
            assertNull(CookieUtil.extractCookie(response.headers, "refreshToken"))
        }
    }
}
