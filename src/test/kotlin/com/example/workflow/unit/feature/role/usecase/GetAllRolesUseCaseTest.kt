package com.example.workflow.unit.feature.role.usecase

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.toViewDto
import com.example.workflow.feature.role.model.RoleViewDto
import com.example.workflow.feature.role.service.RoleService
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
class GetAllRolesUseCaseTest {
    private lateinit var roleService: RoleService
    private lateinit var getAllRolesUseCase: GetAllRolesUseCase

    @BeforeEach
    fun setUp() {
        roleService = mockk()
        getAllRolesUseCase = GetAllRolesUseCase(
            roleService = roleService
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ExecuteFunction() {
        @BeforeEach
        fun setUp() {
            mockkStatic(Role::toViewDto)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(Role::toViewDto)
        }

        @Test
        fun `should return UseCase result of all roles list when multiple roles exist`() {
            // Arrange
            val roles: List<Role> = listOf(mockk(), mockk())
            val roleViewDto0: RoleViewDto = mockk()
            val roleViewDto1: RoleViewDto = mockk()

            every { roleService.getAllRoles() } returns roles
            every { roles[0].toViewDto() } returns roleViewDto0
            every { roles[1].toViewDto() } returns roleViewDto1

            // Act
            val actual = getAllRolesUseCase.execute()

            // Assert
            assertEquals(roleViewDto0, actual.roleViewDtoList[0])
            assertEquals(roleViewDto1, actual.roleViewDtoList[1])
        }

        @Test
        fun `should return UseCase result of an empty list when no roles exist`() {
            // Arrange
            val roles: List<Role> = listOf()

            every { roleService.getAllRoles() } returns roles

            // Act
            val actual = getAllRolesUseCase.execute()

            // Assert
            assertTrue(actual.roleViewDtoList.isEmpty())
        }
    }
}