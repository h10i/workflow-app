package com.example.workflow

import com.example.workflow.test.annotation.E2ETest
import com.example.workflow.test.config.TestcontainersConfiguration
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
    }
}
