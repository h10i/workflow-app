package com.example.workflow.unit.feature.role.presenter

import com.example.workflow.feature.role.model.RoleViewDto
import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.feature.role.model.toViewResponse
import com.example.workflow.feature.role.presenter.GetAllRolesPresenter
import com.example.workflow.feature.role.usecase.GetAllRolesUseCase
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
import kotlin.test.assertTrue

@UnitTest
class GetAllRolesPresenterTest {
    private lateinit var getAllRolePresenter: GetAllRolesPresenter

    @BeforeEach
    fun setUp() {
        getAllRolePresenter = GetAllRolesPresenter()
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ToResponse {
        @BeforeEach
        fun setUp() {
            mockkStatic(RoleViewDto::toViewResponse)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(RoleViewDto::toViewResponse)
        }

        @Test
        fun `should return Presenter result of all roles list when multiple roles exist`() {
            // Arrange
            val roleViewDtoList: List<RoleViewDto> = listOf(mockk(), mockk())
            val useCaseResult = GetAllRolesUseCase.Result(
                roleViewDtoList = roleViewDtoList,
            )
            val roleViewResponse0: RoleViewResponse = mockk()
            val roleViewResponse1: RoleViewResponse = mockk()

            every { roleViewDtoList[0].toViewResponse() } returns roleViewResponse0
            every { roleViewDtoList[1].toViewResponse() } returns roleViewResponse1

            // Act
            val actual: GetAllRolesPresenter.Result = getAllRolePresenter.toResponse(useCaseResult)

            // Assert
            assertEquals(roleViewResponse0, actual.response[0])
            assertEquals(roleViewResponse1, actual.response[1])
        }

        @Test
        fun `should return Presenter result of an empty list when no roles exist`() {
            // Arrange
            val roleViewDtoList: List<RoleViewDto> = listOf()
            val useCaseResult = GetAllRolesUseCase.Result(
                roleViewDtoList = roleViewDtoList,
            )

            // Act
            val actual: GetAllRolesPresenter.Result = getAllRolePresenter.toResponse(useCaseResult)

            // Assert
            assertTrue(actual.response.isEmpty())
        }
    }
}