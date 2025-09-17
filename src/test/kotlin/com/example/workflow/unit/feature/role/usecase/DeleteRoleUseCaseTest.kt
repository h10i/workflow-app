package com.example.workflow.unit.feature.role.usecase

import com.example.workflow.feature.role.service.RoleService
import com.example.workflow.feature.role.usecase.DeleteRoleUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
        fun `should delete the role by id when a role exists`() {
            // Arrange
            val roleId = UUID.randomUUID()

            every { roleService.verifyRoleIdAvailability(roleId) } just runs
            every { roleService.deleteById(roleId) } just runs

            // Act
            deleteRoleUseCase.execute(roleId)

            // Assert
            verify(exactly = 1) { roleService.deleteById(roleId) }
        }

        @Test
        fun `should throw exception when a role does not exist`() {
            // Arrange
            val roleId = UUID.randomUUID()

            every { roleService.verifyRoleIdAvailability(roleId) } throws EntityNotFoundException()
            every { roleService.deleteById(roleId) } just runs

            // Act
            // Assert
            assertThrows<EntityNotFoundException> {
                deleteRoleUseCase.execute(roleId)
            }
            verify(exactly = 0) { roleService.deleteById(roleId) }
        }
    }
}
