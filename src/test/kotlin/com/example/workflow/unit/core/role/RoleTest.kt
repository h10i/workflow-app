package com.example.workflow.unit.core.role

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.toViewDto
import com.example.workflow.support.annotation.UnitTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class RoleTest {

    @Nested
    inner class ToViewDtoFun {
        @Test
        fun `should map role view dto`() {
            // Arrange
            val role = Role(
                id = UUID.randomUUID(),
                name = "EXAMPLE",
            )

            // Act
            val actual = role.toViewDto()

            // Assert
            assertEquals(role.id, actual.id)
            assertEquals(role.name, actual.name)
        }
    }
}
