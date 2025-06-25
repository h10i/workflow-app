package com.example.workflow

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(
    info = Info(
        title = "workflow-app API",
        version = "1.0",
        description = "",
    ),
    servers = [
        Server(
            description = "development",
            url = "http://development.eaxmple.com",
        ),
        Server(
            description = "staging",
            url = "http://staging.eaxmple.com",
        ),
        Server(
            description = "production",
            url = "http://production.eaxmple.com",
        ),
    ]
)
class WorkflowApplication

fun main(args: Array<String>) {
    runApplication<WorkflowApplication>(*args)
}
