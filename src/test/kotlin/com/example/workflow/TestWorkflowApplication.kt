package com.example.workflow

import com.example.workflow.test.config.TestcontainersConfiguration
import org.springframework.boot.fromApplication
import org.springframework.boot.with
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class TestWorkflowApplication

fun main(args: Array<String>) {
    fromApplication<WorkflowApplication>().with(TestcontainersConfiguration::class).run(*args)
}