package com.example.workflow.unit.feature.account.model

import com.example.workflow.feature.account.model.UpdateAccountRequest
import com.example.workflow.support.annotation.UnitTest
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@UnitTest
class UpdateAccountRequestTest {
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
    inner class EmailAddress {
        @Test
        fun `success (skip validation) when emailAddress is blank`() {
            // Arrange
            val request = UpdateAccountRequest()

            // Act
            val violations = validator.validate(request)

            // Assert
            val emailAddressViolations =
                violations.filter { it.propertyPath.toString() == UpdateAccountRequest::emailAddress.name }
            assertTrue(emailAddressViolations.isEmpty())
        }

        @Test
        fun `success when emailAddress are valid`() {
            // Arrange
            val request = UpdateAccountRequest(emailAddress = "test@example.com")

            // Act
            val violations = validator.validate(request)

            // Assert
            val emailAddressViolations =
                violations.filter { it.propertyPath.toString() == UpdateAccountRequest::emailAddress.name }
            assertTrue(emailAddressViolations.isEmpty())
        }

        @Test
        fun `failure when emailAddress has an invalid format`() {
            // Arrange
            val request = UpdateAccountRequest(emailAddress = "invalid-email")

            // Act
            val violations = validator.validate(request)

            // Assert
            val emailAddressViolations =
                violations.filter { it.propertyPath.toString() == UpdateAccountRequest::emailAddress.name }
            assertFalse(emailAddressViolations.isEmpty())
            assertTrue(emailAddressViolations.any { it.message == "Invalid email address format" })
        }
    }

    @Nested
    inner class Password {
        @Test
        fun `success (skip validation) when password is blank`() {
            // Arrange
            val request = UpdateAccountRequest()

            // Act
            val violations = validator.validate(request)

            // Assert
            val passwordViolations =
                violations.filter { it.propertyPath.toString() == UpdateAccountRequest::password.name }
            assertTrue(passwordViolations.isEmpty())
        }

        @Test
        fun `success when password are valid`() {
            // Arrange
            val request = UpdateAccountRequest(password = "P4sSw0rd!")

            // Act
            val violations = validator.validate(request)

            // Assert
            val passwordViolations =
                violations.filter { it.propertyPath.toString() == UpdateAccountRequest::password.name }
            assertTrue(passwordViolations.isEmpty())
        }
    }
}