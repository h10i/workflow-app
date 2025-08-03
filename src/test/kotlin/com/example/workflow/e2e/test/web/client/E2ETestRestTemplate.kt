package com.example.workflow.e2e.test.web.client

import com.example.workflow.e2e.test.util.CookieUtil
import com.example.workflow.feature.token.model.TokenResponse
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*

class E2ETestRestTemplate(
    private val restTemplate: TestRestTemplate
) {
    data class JsonResponse<T>(
        val body: T?,
        val statusCode: HttpStatusCode,
        val headers: HttpHeaders,
    )

    data class AuthResult(
        val accessToken: String,
        val refreshToken: String,
    )

    fun <T> exchange(
        responseType: Class<T>,
        method: HttpMethod,
        path: String,
        body: Any? = null,
        accessToken: String? = null,
        cookie: String? = null,
        headers: HttpHeaders = HttpHeaders()
    ): JsonResponse<T> {
        if (!headers.containsKey(HttpHeaders.ACCEPT)) {
            headers.accept = listOf(MediaType.APPLICATION_JSON)
        }
        if (accessToken != null) {
            headers.setBearerAuth(accessToken)
        }
        if (cookie != null) {
            headers.add("Cookie", cookie)
        }
        if (body != null && !headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            headers.contentType = MediaType.APPLICATION_JSON
        }

        val request = HttpEntity(body, headers)

        val response = restTemplate.exchange(
            path,
            method,
            request,
            responseType
        )

        return JsonResponse(
            body = response.body,
            statusCode = response.statusCode,
            headers = response.headers
        )
    }

    fun <T> get(
        responseType: Class<T>,
        path: String,
        accessToken: String? = null,
        cookie: String? = null,
        headers: HttpHeaders = HttpHeaders()
    ): JsonResponse<T> = exchange(responseType, HttpMethod.GET, path, null, accessToken, cookie, headers)

    fun <T> post(
        responseType: Class<T>,
        path: String,
        body: Any,
        accessToken: String? = null,
        cookie: String? = null,
        headers: HttpHeaders = HttpHeaders()
    ): JsonResponse<T> = exchange(responseType, HttpMethod.POST, path, body, accessToken, cookie, headers)

    fun <T> put(
        responseType: Class<T>,
        path: String,
        body: Any,
        accessToken: String? = null,
        cookie: String? = null,
        headers: HttpHeaders = HttpHeaders()
    ): JsonResponse<T> = exchange(responseType, HttpMethod.PUT, path, body, accessToken, cookie, headers)

    fun <T> patch(
        responseType: Class<T>,
        path: String,
        body: Any,
        accessToken: String? = null,
        cookie: String? = null,
        headers: HttpHeaders = HttpHeaders()
    ): JsonResponse<T> = exchange(responseType, HttpMethod.PATCH, path, body, accessToken, cookie, headers)

    fun <T> delete(
        responseType: Class<T>,
        path: String,
        accessToken: String? = null,
        cookie: String? = null,
        headers: HttpHeaders = HttpHeaders()
    ): JsonResponse<T> = exchange(responseType, HttpMethod.DELETE, path, null, accessToken, cookie, headers)

    fun authenticate(
        emailAddress: String = "test@example.com",
        password: String = "PASSWORD"
    ): AuthResult {
        val json = """
            {
              "emailAddress": "$emailAddress",
              "password": "$password"
            }
        """.trimIndent()

        val response = post(
            responseType = TokenResponse::class.java,
            path = "/v1/auth/token",
            body = json
        )

        val accessToken = response.body?.accessToken
            ?: throw IllegalStateException("access token not found")
        val refreshToken = CookieUtil.extractCookie(response.headers, "refreshToken")
            ?: throw IllegalArgumentException("refresh token cookie not found")
        return AuthResult(
            accessToken,
            refreshToken,
        )
    }
}