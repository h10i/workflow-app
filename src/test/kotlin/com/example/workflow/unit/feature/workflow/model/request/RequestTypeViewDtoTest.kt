package com.example.workflow.unit.feature.workflow.model.request

import com.example.workflow.feature.workflow.model.request.RequestTypeViewDto
import com.example.workflow.feature.workflow.model.request.toViewResponse
import com.example.workflow.support.annotation.UnitTest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class RequestTypeViewDtoTest {
    private val objectMapper = jacksonObjectMapper()

    @Nested
    inner class ToViewResponseFun {
        @Test
        fun `should map request type view response`() {
            // Arrange
            val requestTypeViewDto = RequestTypeViewDto(
                id = UUID.randomUUID(),
                name = "test name",
                description = "test description",
                schemaDefinition = objectMapper.readTree("""{"foo":"bar"}"""),
            )

            // Act
            val actual = requestTypeViewDto.toViewResponse()

            // Assert
            assertEquals(requestTypeViewDto.id, actual.id)
            assertEquals(requestTypeViewDto.name, actual.name)
            assertEquals(requestTypeViewDto.description, actual.description)
            assertEquals(requestTypeViewDto.schemaDefinition, actual.schemaDefinition)
        }
    }
}
