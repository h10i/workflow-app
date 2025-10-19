package com.example.workflow.unit.core.workflow.request

import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.core.workflow.request.toViewDto
import com.example.workflow.support.annotation.UnitTest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class RequestTypeTest {
    private val objectMapper = jacksonObjectMapper()

    @Nested
    inner class ToViewDtoFun {
        @Test
        fun `should map request type view dto`() {
            // Arrange
            val requestType = RequestType(
                id = UUID.randomUUID(),
                name = "test name",
                description = "test description",
                schemaDefinition = objectMapper.readTree("""{"foo":"bar"}"""),
            )

            // Act
            val actual = requestType.toViewDto()

            // Assert
            assertEquals(requestType.id, actual.id)
            assertEquals(requestType.name, actual.name)
            assertEquals(requestType.description, actual.description)
            assertEquals(requestType.schemaDefinition, actual.schemaDefinition)
        }
    }
}
