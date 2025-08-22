package com.example.workflow.e2e.test.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
@Profile("test")
@Suppress("unused")
class TestDataInitializer(
    private val dataSource: DataSource
) {
    @PostConstruct
    fun init() {
        val sql = javaClass.getResource("/db/init-test-data.sql")!!.readText()
        dataSource.connection.use { connection ->
            val stmt = connection.createStatement()
            for (statement in sql.split(";")) {
                val trimmed = statement.trim()
                if (trimmed.isNotEmpty()) {
                    stmt.execute(trimmed)
                }
            }
        }
    }
}