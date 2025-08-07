package com.example.workflow.unit.feature.role.usecase

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.toViewDto
import com.example.workflow.feature.role.model.CreateRoleRequest
import com.example.workflow.feature.role.model.RoleViewDto
import com.example.workflow.feature.role.service.RoleService
import com.example.workflow.feature.role.usecase.CreateRoleUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@UnitTest
class CreateRoleUseCaseTest {
    private lateinit var roleService: RoleService
    private lateinit var createRoleUseCase: CreateRoleUseCase

    @BeforeEach
    fun setUp() {
        roleService = mockk()
        createRoleUseCase = CreateRoleUseCase(
            roleService = roleService,
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ExecuteMethod {
        @BeforeEach
        fun setUp() {
            mockkStatic(Role::toViewDto)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(Role::toViewDto)
        }

        @Test
        fun `return role with valid request`() {
            // Arrange
            val roleName = "EXAMPLE"
            val request = CreateRoleRequest(
                name = roleName,
            )
            val savedRole: Role = mockk()
            val claimsSet = slot<Role>()
            every { roleService.saveRole(capture(claimsSet)) } returns savedRole

            val roleViewDto: RoleViewDto = mockk()
            every { savedRole.toViewDto() } returns roleViewDto

            // Act
            val actual = createRoleUseCase.execute(request)

            // Assert
            val capturedRole = claimsSet.captured
            assertEquals(roleName, capturedRole.name)

            assertEquals(roleViewDto, actual.roleViewDto)
        }
    }
}