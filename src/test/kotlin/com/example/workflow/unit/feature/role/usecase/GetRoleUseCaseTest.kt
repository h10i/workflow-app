package com.example.workflow.unit.feature.role.usecase

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.toViewDto
import com.example.workflow.feature.role.exception.RoleNotFoundException
import com.example.workflow.feature.role.model.RoleViewDto
import com.example.workflow.feature.role.service.RoleService
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
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class GetRoleUseCaseTest {
    private lateinit var roleService: RoleService
    private lateinit var getRoleUseCase: GetRoleUseCase

    @BeforeEach
    fun setUp() {
        roleService = mockk()
        getRoleUseCase = GetRoleUseCase(
            roleService = roleService
        )
    }

    @Nested
    inner class ExecuteFun {
        @BeforeEach
        fun setUp() {
            mockkStatic(Role::toViewDto)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(Role::toViewDto)
        }

        @Test
        fun `should return UseCase result of a role when a role exists`() {
            // Arrange
            val roleId = UUID.randomUUID()
            val role: Role = mockk()
            val roleViewDto: RoleViewDto = mockk()
            every { role.toViewDto() } returns roleViewDto
            every { roleService.getRoleById(roleId) } returns role

            // Act
            val actual = getRoleUseCase.execute(roleId)

            // Assert
            assertEquals(roleViewDto, actual.roleViewDto)
        }

        @Test
        fun `should throw RoleNotFoundException when a role does not exists`() {
            // Arrange
            val roleId = UUID.randomUUID()
            every { roleService.getRoleById(roleId) } returns null

            // Act
            val actual = assertThrows<RoleNotFoundException> {
                getRoleUseCase.execute(roleId)
            }

            // Assert
            assertEquals("Role not found (id: $roleId)", actual.message)
        }
    }
}
