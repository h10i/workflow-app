package com.example.workflow.e2e.test.web.model

import org.springframework.http.HttpHeaders

data class HttpRequestOptions(
    val body: Any? = null,
    val accessToken: String? = null,
    val cookie: String? = null,
    val headers: HttpHeaders = HttpHeaders()
)
