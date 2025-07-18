package com.example.workflow.e2e.feature.account

import com.example.workflow.e2e.test.config.TestcontainersConfiguration
import com.example.workflow.e2e.test.web.client.E2ETestRestTemplate
import com.example.workflow.support.annotation.E2ETest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
class AccountApiTest(
    @Autowired
    private val restTemplate: E2ETestRestTemplate
) {
    @Nested
    inner class GetAccount {
        @Test
        fun `GET account with valid credentials returns 200 OK`() {
            // Arrange
            val authResult: E2ETestRestTemplate.AuthResult = restTemplate.authenticate()

            // Act
            val response = restTemplate.get(
                responseType = String::class.java,
                path = "/v1/accounts/me",
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
                path = "/v1/accounts/me",
            )

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
            assertNull(response.body)
        }
    }
}