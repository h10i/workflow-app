package com.example.workflow.e2e

import com.example.workflow.e2e.test.config.TestcontainersConfiguration
import com.example.workflow.support.annotation.E2ETest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@E2ETest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
@ActiveProfiles("test")
class WorkflowApplicationTests {
    @Test
    fun contextLoads() {
        // Verifies that the Spring application context can be loaded successfully.
        // If context startup fails (e.g. due to missing beans), this test will fail.
    }
}