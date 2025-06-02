package com.example.workflow

import com.example.workflow.infra.security.model.RsaKeyProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties::class)
class WorkflowApplication

fun main(args: Array<String>) {
    runApplication<WorkflowApplication>(*args)
}
