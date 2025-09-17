package com.example.workflow.unit.feature.role.presenter

import com.example.workflow.feature.role.model.RoleViewDto
import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.feature.role.model.toViewResponse
import com.example.workflow.feature.role.presenter.RolePresenter
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@UnitTest
class RolePresenterTest {
    private lateinit var rolePresenter: RolePresenter

    @BeforeEach
    fun setUp() {
        rolePresenter = RolePresenter()
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ToResponseFun {
        @BeforeEach
        fun setUp() {
            mockkStatic(RoleViewDto::toViewResponse)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(RoleViewDto::toViewResponse)
        }

        @Test
        fun `should return a presenter result`() {
            // Arrange
            val roleViewDto: RoleViewDto = mockk()
            val roleViewResponse: RoleViewResponse = mockk()

            every { roleViewDto.toViewResponse() } returns roleViewResponse

            // Act
            val actual = rolePresenter.toResponse(roleViewDto)

            // Assert
            assertEquals(roleViewResponse, actual.response)
        }
    }
}
