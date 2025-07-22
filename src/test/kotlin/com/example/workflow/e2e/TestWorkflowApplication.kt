package com.example.workflow.e2e

import com.example.workflow.WorkflowApplication
import com.example.workflow.e2e.test.config.TestcontainersConfiguration
import org.springframework.boot.fromApplication
import org.springframework.boot.with

class TestWorkflowApplication

fun main(args: Array<String>) {
    fromApplication<WorkflowApplication>()
        .with(TestcontainersConfiguration::class)
        .withAdditionalProfiles("test")
        .run(*args)
}