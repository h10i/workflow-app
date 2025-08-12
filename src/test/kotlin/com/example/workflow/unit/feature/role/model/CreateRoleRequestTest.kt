package com.example.workflow.unit.feature.role.model

import com.example.workflow.feature.role.model.CreateRoleRequest
import com.example.workflow.support.annotation.UnitTest
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

@UnitTest
class CreateRoleRequestTest {
    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val validatorFactory = Validation.buildDefaultValidatorFactory()
        validator = validatorFactory.validator
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class Name {
        @Test
        fun `success when name are valid`() {
            // Arrange
            val request = CreateRoleRequest(name = "EXAMPLE")

            // Act
            val violations = validator.validate(request)

            // Assert
            val nameViolations =
                violations.filter { it.propertyPath.toString() == CreateRoleRequest::name.name }
            assertTrue(nameViolations.isEmpty())
        }

        @Test
        fun `failure when name is blank`() {
            // Arrange
            val request = CreateRoleRequest(name = "")

            // Act
            val violations = validator.validate(request)

            // Assert
            val nameViolations =
                violations.filter { it.propertyPath.toString() == CreateRoleRequest::name.name }
            assertFalse(nameViolations.isEmpty())
            assertTrue(nameViolations.any { it.message == "Name must not be blank" })
        }
    }
}