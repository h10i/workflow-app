package com.example.workflow.e2e.test.util

import org.springframework.http.HttpHeaders

object CookieUtil {
    fun extractCookie(headers: HttpHeaders, cookieName: String): String? {
        return headers[HttpHeaders.SET_COOKIE]
            ?.firstOrNull { it.startsWith("${cookieName}=") }
            ?.substringAfter("=")
            ?.substringBefore(";")
    }
}