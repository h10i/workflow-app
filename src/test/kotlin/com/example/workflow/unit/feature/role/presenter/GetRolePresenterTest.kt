package com.example.workflow.unit.feature.role.presenter

import com.example.workflow.feature.role.model.RoleViewDto
import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.feature.role.model.toViewResponse
import com.example.workflow.feature.role.presenter.GetRolePresenter
import com.example.workflow.feature.role.usecase.GetRoleUseCase
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
class GetRolePresenterTest {
    private lateinit var getRolePresenter: GetRolePresenter

    @BeforeEach
    fun setUp() {
        getRolePresenter = GetRolePresenter()
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ToResponseFunction {
        @BeforeEach
        fun setUp() {
            mockkStatic(RoleViewDto::toViewResponse)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(RoleViewDto::toViewResponse)
        }

        @Test
        fun `should return Presenter result of a role when a role exists`() {
            // Arrange
            val roleViewDto: RoleViewDto = mockk()
            val useCaseResult = GetRoleUseCase.Result(
                roleViewDto = roleViewDto,
            )
            val roleViewResponse: RoleViewResponse = mockk()

            every { roleViewDto.toViewResponse() } returns roleViewResponse

            // Act
            val actual = getRolePresenter.toResponse(useCaseResult)

            // Assert
            assertEquals(roleViewResponse, actual.response)
        }
    }
}