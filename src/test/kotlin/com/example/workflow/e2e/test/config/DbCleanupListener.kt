package com.example.workflow.e2e.test.config

import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import javax.sql.DataSource

class DbCleanupListener : TestExecutionListener {
    override fun beforeTestMethod(testContext: TestContext) {
        val dataSource = testContext.applicationContext.getBean(DataSource::class.java)
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