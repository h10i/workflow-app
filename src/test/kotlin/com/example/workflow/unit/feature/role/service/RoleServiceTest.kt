package com.example.workflow.unit.feature.role.service

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.RoleRepository
import com.example.workflow.feature.role.service.RoleService
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
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
}