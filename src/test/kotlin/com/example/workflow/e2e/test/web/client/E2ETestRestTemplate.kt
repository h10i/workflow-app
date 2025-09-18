package com.example.workflow.e2e.test.web.client

import com.example.workflow.common.path.ApiPath
import com.example.workflow.e2e.test.util.CookieUtil
import com.example.workflow.e2e.test.web.model.HttpRequestOptions
import com.example.workflow.feature.account.model.AccountViewResponse
import com.example.workflow.feature.auth.model.TokenResponse
import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.support.util.TestDataFactory
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType

class E2ETestRestTemplate(
    internal val restTemplate: TestRestTemplate
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
        httpRequestOptions: HttpRequestOptions,
    ): JsonResponse<T> {
        if (!httpRequestOptions.headers.containsKey(HttpHeaders.ACCEPT)) {
            httpRequestOptions.headers.accept = listOf(MediaType.APPLICATION_JSON)
        }
        if (httpRequestOptions.accessToken != null) {
            httpRequestOptions.headers.setBearerAuth(httpRequestOptions.accessToken)
        }
        if (httpRequestOptions.cookie != null) {
            httpRequestOptions.headers.add("Cookie", httpRequestOptions.cookie)
        }
        if (httpRequestOptions.body != null && !httpRequestOptions.headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            httpRequestOptions.headers.contentType = MediaType.APPLICATION_JSON
        }

        val request = HttpEntity(httpRequestOptions.body, httpRequestOptions.headers)

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
        httpRequestOptions: HttpRequestOptions = HttpRequestOptions()
    ): JsonResponse<T> = exchange(responseType, HttpMethod.GET, path, httpRequestOptions)

    fun <T> post(
        responseType: Class<T>,
        path: String,
        httpRequestOptions: HttpRequestOptions
    ): JsonResponse<T> = exchange(responseType, HttpMethod.POST, path, httpRequestOptions)

    fun <T> put(
        responseType: Class<T>,
        path: String,
        httpRequestOptions: HttpRequestOptions
    ): JsonResponse<T> = exchange(responseType, HttpMethod.PUT, path, httpRequestOptions)

    fun <T> patch(
        responseType: Class<T>,
        path: String,
        httpRequestOptions: HttpRequestOptions
    ): JsonResponse<T> = exchange(responseType, HttpMethod.PATCH, path, httpRequestOptions)

    fun <T> delete(
        responseType: Class<T>,
        path: String,
        httpRequestOptions: HttpRequestOptions
    ): JsonResponse<T> = exchange(responseType, HttpMethod.DELETE, path, httpRequestOptions)

    fun registerAccount(
        emailAddress: String = TestDataFactory.createUniqueEmailAddress(),
        password: String = TestDataFactory.getValidTestPassword(),
    ): AccountViewResponse {
        val json = """
            {
              "emailAddress": "$emailAddress",
              "password": "$password"
            }
        """.trimIndent()

        val response = post(
            responseType = AccountViewResponse::class.java,
            path = ApiPath.Account.BASE,
            httpRequestOptions = HttpRequestOptions(
                body = json,
            )
        )
        check(response.statusCode == HttpStatus.CREATED && response.body != null) {
            "Failed to register user for test: ${response.statusCode} - ${response.body}"
        }
        return response.body
    }

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
            httpRequestOptions = HttpRequestOptions(
                body = json
            )
        )

        val accessToken = response.body?.accessToken
            ?: error("access token not found")
        val refreshToken = CookieUtil.extractCookie(response.headers, "refreshToken")
            ?: error("refresh token cookie not found")
        return AuthResult(
            accessToken,
            refreshToken,
        )
    }

    fun authenticateWithAdmin(): AuthResult = authenticate("admin@example.com", "PASSWORD")

    fun registerAccountAndAuthenticate(
        emailAddress: String = TestDataFactory.createUniqueEmailAddress(),
        password: String = TestDataFactory.getValidTestPassword(),
    ): AuthResult {
        registerAccount(emailAddress = emailAddress, password = password)
        return authenticate(emailAddress = emailAddress, password = password)
    }

    fun createRole(
        name: String,
        accessToken: String,
    ): RoleViewResponse {
        val json = """
                {
                    "name": "$name"
                }
        """.trimIndent()

        val response = post(
            responseType = RoleViewResponse::class.java,
            path = ApiPath.Role.BASE,
            httpRequestOptions = HttpRequestOptions(
                body = json,
                accessToken = accessToken,
            )
        )

        return response.body ?: error("role not found")
    }
}
