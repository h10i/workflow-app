package com.example.workflow.unit.feature.role.model

import com.example.workflow.feature.role.model.RoleViewDto
import com.example.workflow.feature.role.model.toViewResponse
import com.example.workflow.support.annotation.UnitTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class RoleViewDtoTest {
    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ToViewResponse {
        @Test
        fun `should map role view response`() {
            // Arrange
            val roleViewDto = RoleViewDto(
                id = UUID.randomUUID(),
                name = "EXAMPLE",
            )

            // Act
            val actual = roleViewDto.toViewResponse()

            // Assert
            assertEquals(roleViewDto.id, actual.id)
            assertEquals(roleViewDto.name, actual.name)
        }
    }
}