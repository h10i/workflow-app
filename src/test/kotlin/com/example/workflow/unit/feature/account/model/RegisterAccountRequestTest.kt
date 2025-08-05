package com.example.workflow.unit.feature.account.model

import com.example.workflow.feature.account.model.RegisterAccountRequest
import com.example.workflow.support.annotation.UnitTest
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@UnitTest
class RegisterAccountRequestTest {
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
        fun `success when emailAddress are valid`() {
            // Arrange
            val request = RegisterAccountRequest(emailAddress = "test@example.com", password = "")

            // Act
            val violations = validator.validate(request)

            // Assert
            val emailAddressViolations =
                violations.filter { it.propertyPath.toString() == RegisterAccountRequest::emailAddress.name }
            assertTrue(emailAddressViolations.isEmpty())
        }

        @Test
        fun `failure when emailAddress is blank`() {
            // Arrange
            val request = RegisterAccountRequest(emailAddress = "", password = "P4sSw0rd!")

            // Act
            val violations = validator.validate(request)

            // Assert
            val emailAddressViolations =
                violations.filter { it.propertyPath.toString() == RegisterAccountRequest::emailAddress.name }
            assertFalse(emailAddressViolations.isEmpty())
            assertTrue(emailAddressViolations.any { it.message == "Email address must not be blank" })
        }

        @Test
        fun `failure when emailAddress has an invalid format`() {
            // Arrange
            val request = RegisterAccountRequest(emailAddress = "invalid-email", password = "P4sSw0rd!")

            // Act
            val violations = validator.validate(request)

            // Assert
            val emailAddressViolations =
                violations.filter { it.propertyPath.toString() == RegisterAccountRequest::emailAddress.name }
            assertFalse(emailAddressViolations.isEmpty())
            assertTrue(emailAddressViolations.any { it.message == "Invalid email address format" })
        }
    }

    @Nested
    inner class Password {
        @Test
        fun `success when password are valid`() {
            // Arrange
            val request = RegisterAccountRequest(emailAddress = "", password = "P4sSw0rd!")

            // Act
            val violations = validator.validate(request)

            // Assert
            val passwordViolations =
                violations.filter { it.propertyPath.toString() == RegisterAccountRequest::password.name }
            assertTrue(passwordViolations.isEmpty())
        }

        @Test
        fun `failure when password is blank`() {
            // Arrange
            val request = RegisterAccountRequest(emailAddress = "test@example.com", password = "")

            // Act
            val violations = validator.validate(request)

            // Assert
            val passwordViolations =
                violations.filter { it.propertyPath.toString() == RegisterAccountRequest::password.name }
            assertFalse(passwordViolations.isEmpty())
            assertTrue(passwordViolations.any { it.message == "password must not be blank" })
        }
    }
}