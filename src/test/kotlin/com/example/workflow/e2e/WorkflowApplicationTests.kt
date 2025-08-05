package com.example.workflow.e2e

import com.example.workflow.e2e.test.base.AbstractE2ETest
import com.example.workflow.support.annotation.E2ETest
import org.junit.jupiter.api.Test

@E2ETest
class WorkflowApplicationTests : AbstractE2ETest() {
    @Test
    fun contextLoads() {
        // Verifies that the Spring application context can be loaded successfully.
        // If context startup fails (e.g. due to missing beans), this test will fail.
    }
}