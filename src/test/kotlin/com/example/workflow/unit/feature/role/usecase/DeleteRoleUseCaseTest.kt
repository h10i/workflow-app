package com.example.workflow.unit.feature.role.usecase

import com.example.workflow.feature.role.service.RoleService
import com.example.workflow.feature.role.usecase.DeleteRoleUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

@UnitTest
class DeleteRoleUseCaseTest {
    private lateinit var roleService: RoleService
    private lateinit var deleteRoleUseCase: DeleteRoleUseCase

    @BeforeEach
    fun setUp() {
        roleService = mockk()
        deleteRoleUseCase = DeleteRoleUseCase(
            roleService = roleService,
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ExecuteFun {
        @Test
        fun `should delete role by id`() {
            // Arrange
            val roleId = UUID.randomUUID()

            every { roleService.deleteById(roleId) } just runs

            // Act
            deleteRoleUseCase.execute(roleId)

            // Assert
            verify(exactly = 1) { roleService.deleteById(roleId) }
        }
    }
}