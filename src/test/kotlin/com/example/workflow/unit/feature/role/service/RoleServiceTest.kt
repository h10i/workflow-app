package com.example.workflow.unit.feature.role.service

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.RoleRepository
import com.example.workflow.feature.role.exception.RoleNameAlreadyCreatedException
import com.example.workflow.feature.role.service.RoleService
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@UnitTest
class RoleServiceTest {
    private lateinit var roleRepository: RoleRepository
    private lateinit var roleService: RoleService

    @BeforeEach
    fun setUp() {
        roleRepository = mockk()
        roleService = RoleService(roleRepository)
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class SaveRole {
        @Test
        fun `return role when saving new role`() {
            // Arrange
            val role: Role = mockk()
            val savedRole: Role = mockk()

            every { roleRepository.save(role) } returns savedRole

            // Act
            val actual: Role = roleService.saveRole(role)

            // Assert
            assertEquals(savedRole, actual)
        }
    }

    @Nested
    inner class VerifyRoleAvailability {
        @Test
        fun `throws RoleNameAlreadyCreatedException when email address is created`() {
            // Arrange
            val roleName = "EXAMPLE"
            val role: Role = mockk()
            every { roleRepository.findByName(roleName) } returns role

            // Act
            // Assert
            val actualException = assertThrows<RoleNameAlreadyCreatedException> {
                roleService.verifyRoleAvailability(roleName)
            }
            assertEquals(Role::name.name, actualException.field)
            assertEquals("This role name is already created.", actualException.message)
        }

        @Test
        fun `does not throws RoleNameAlreadyCreatedException when email address is not created`() {
            // Arrange
            val roleName = "EXAMPLE"
            every { roleRepository.findByName(roleName) } returns null

            // Act
            // Assert
            assertDoesNotThrow {
                roleService.verifyRoleAvailability(roleName)
            }
            verify(exactly = 1) { roleRepository.findByName(roleName) }
        }
    }
}