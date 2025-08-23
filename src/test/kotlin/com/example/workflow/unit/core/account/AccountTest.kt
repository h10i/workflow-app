package com.example.workflow.unit.core.account

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.toViewDto
import com.example.workflow.support.annotation.UnitTest
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class AccountTest {
    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ToViewDto {
        @Test
        fun `should map account view dto`() {
            // Arrange
            val account = Account(
                id = UUID.randomUUID(),
                emailAddress = "test@example.com",
                password = "test-password",
                roles = mutableListOf(mockk(relaxed = true)),
                refreshTokens = mutableListOf(mockk(relaxed = true)),
            )

            // Act
            val actual = account.toViewDto()

            // Assert
            assertEquals(account.id, actual.id)
            assertEquals(account.emailAddress, actual.emailAddress)
            assertEquals(1, actual.roleNames.size)
            assertEquals(account.roles[0].role.name, actual.roleNames[0])
        }
    }
}