package com.example.workflow.e2e.test.config

import com.example.workflow.e2e.test.web.client.E2ETestRestTemplate
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@TestConfiguration
@Profile("test")
class E2ETestConfig {
    @Bean
    fun e2ETestRestTemplate(testRestTemplate: TestRestTemplate): E2ETestRestTemplate {
        return E2ETestRestTemplate(testRestTemplate)
    }
}