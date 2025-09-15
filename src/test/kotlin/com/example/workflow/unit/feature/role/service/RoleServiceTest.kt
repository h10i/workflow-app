package com.example.workflow.unit.feature.role.service

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.RoleRepository
import com.example.workflow.feature.role.exception.RoleNameAlreadyCreatedException
import com.example.workflow.feature.role.exception.RoleNotFoundException
import com.example.workflow.feature.role.service.RoleService
import com.example.workflow.support.annotation.UnitTest
import com.example.workflow.support.util.TestDataFactory
import io.mockk.*
import org.junit.jupiter.api.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
    inner class SaveRoleFun {
        @Test
        fun `should return the role when saving a new role`() {
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
    inner class GetRoleByIdFun {
        @Test
        fun `should return the role when a role exists`() {
            // Arrange
            val roleId = UUID.randomUUID()
            val role = TestDataFactory.createRole(id = roleId)

            every { roleRepository.findById(roleId) } returns Optional.of(role)

            // Act
            val actual: Role? = roleService.getRoleById(roleId)

            // Assert
            assertEquals(role, actual)
        }

        @Test
        fun `should return null when a role does not exist`() {
            // Arrange
            val roleId = UUID.randomUUID()

            every { roleRepository.findById(roleId) } returns Optional.empty()

            // Act
            val actual: Role? = roleService.getRoleById(roleId)

            // Assert
            assertNull(actual)
        }
    }

    @Nested
    inner class GetAllRolesFun {
        @Test
        fun `should return a list of all roles`() {
            // Arrange
            val roles: List<Role> = mockk()

            every { roleRepository.findAll() } returns roles

            // Act
            val actual = roleService.getAllRoles()

            // Assert
            assertEquals(roles, actual)
        }
    }

    @Nested
    inner class DeleteRoleByIdFun {
        @Test
        fun `should delete role by id`() {
            // Arrange
            val roleId: UUID = UUID.randomUUID()
            every { roleRepository.deleteById(roleId) } just runs

            // Act
            roleService.deleteById(roleId)

            // Assert
            verify(exactly = 1) { roleRepository.deleteById(roleId) }
        }
    }

    @Nested
    inner class VerifyRoleIdAvailabilityFun {
        @Test
        fun `should throw RoleNotFoundException when a role does not exist`() {
            // Arrange
            val roleId = UUID.randomUUID()

            every { roleRepository.findById(roleId) } returns Optional.empty()

            // Act
            // Assert
            val actual = assertThrows<RoleNotFoundException> {
                roleService.verifyRoleIdAvailability(roleId)
            }
            assertEquals("Role not found with criteria: id: $roleId", actual.message)
        }

        @Test
        fun `should not throw any Exception when a role exists`() {
            // Arrange
            val roleId = UUID.randomUUID()
            val role = TestDataFactory.createRole(id = roleId)

            every { roleRepository.findById(roleId) } returns Optional.of(role)

            // Act
            // Assert
            assertDoesNotThrow {
                roleService.verifyRoleIdAvailability(roleId)
            }
        }
    }

    @Nested
    inner class VerifyRoleNameAvailabilityFun {
        @Test
        fun `should throw RoleNameAlreadyCreatedException when email address is created`() {
            // Arrange
            val roleName = "EXAMPLE"
            val role: Role = mockk()
            every { roleRepository.findByName(roleName) } returns role

            // Act
            // Assert
            val actualException = assertThrows<RoleNameAlreadyCreatedException> {
                roleService.verifyRoleNameAvailability(roleName)
            }
            assertEquals(Role::name.name, actualException.field)
            assertEquals("This role name is already created.", actualException.message)
        }

        @Test
        fun `should not throw RoleNameAlreadyCreatedException when email address is not created`() {
            // Arrange
            val roleName = "EXAMPLE"
            every { roleRepository.findByName(roleName) } returns null

            // Act
            // Assert
            assertDoesNotThrow {
                roleService.verifyRoleNameAvailability(roleName)
            }
            verify(exactly = 1) { roleRepository.findByName(roleName) }
        }
    }
}