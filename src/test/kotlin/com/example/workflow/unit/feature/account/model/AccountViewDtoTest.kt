package com.example.workflow.unit.feature.account.model

import com.example.workflow.feature.account.model.AccountViewDto
import com.example.workflow.feature.account.model.toViewResponse
import com.example.workflow.support.annotation.UnitTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class AccountViewDtoTest {
    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ToViewResponse {
        @Test
        fun `should map account view dto`() {
            // Arrange
            val accountViewDto = AccountViewDto(
                id = UUID.randomUUID(),
                emailAddress = "test@example.com",
                roleNames = listOf("EXAMPLE"),
            )

            // Act
            val actual = accountViewDto.toViewResponse()

            // Assert
            assertEquals(accountViewDto.id, actual.id)
            assertEquals(accountViewDto.emailAddress, actual.emailAddress)
            assertEquals(1, actual.roleNames.size)
            assertEquals(accountViewDto.roleNames[0], actual.roleNames[0])
        }
    }
}