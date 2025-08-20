package com.example.workflow.e2e.test.base

import com.example.workflow.e2e.test.config.DbCleanupListener
import com.example.workflow.e2e.test.config.E2ETestConfig
import com.example.workflow.e2e.test.config.TestcontainersConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(
    listeners = [DbCleanupListener::class],
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@Import(TestcontainersConfiguration::class, E2ETestConfig::class)
@ActiveProfiles("test")
abstract class AbstractE2ETest {
}