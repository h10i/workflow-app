package com.example.workflow.unit.feature.workflow.model.request

import com.example.workflow.feature.workflow.model.request.CreateRequestTypeRequest
import com.example.workflow.support.annotation.UnitTest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@UnitTest
class CreateRequestTypeRequestTest {
    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val validatorFactory = Validation.buildDefaultValidatorFactory()
        validator = validatorFactory.validator
    }

    @Nested
    inner class NameValidation {
        @Test
        fun `should succeed when name is valid`() {
            // Arrange
            val request = CreateRequestTypeRequest(
                name = "test",
                description = "",
                schemaDefinition = jacksonObjectMapper().readTree("""{"foo":"bar"}""")
            )

            // Act
            val violations = validator.validate(request)

            // Assert
            val nameViolations =
                violations.filter { it.propertyPath.toString() == CreateRequestTypeRequest::name.name }
            assertTrue(nameViolations.isEmpty())
        }

        @Test
        fun `should fail when name is blank`() {
            // Arrange
            val request = CreateRequestTypeRequest(
                name = "",
                description = "",
                schemaDefinition = jacksonObjectMapper().readTree("""{"foo":"bar"}""")
            )

            // Act
            val violations = validator.validate(request)

            // Assert
            val nameViolations =
                violations.filter { it.propertyPath.toString() == CreateRequestTypeRequest::name.name }
            assertTrue(nameViolations.isNotEmpty())
            assertTrue(nameViolations.any { it.message == "Name must not be blank" })
        }
    }

    // todo: @ValidJsonSchemaDefinition Tests
}
