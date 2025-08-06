package com.example.workflow.integration.core.role

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.RoleRepository
import com.example.workflow.support.annotation.IntegrationTest
import com.example.workflow.support.util.TestDataFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import kotlin.test.assertEquals

@IntegrationTest
@DataJpaTest

class RoleRepositoryTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class Save {
        @Test
        fun `returns role when saving new role`() {
            // Arrange
            val role = TestDataFactory.createRole()

            // Act
            val actual: Role = roleRepository.save(role)

            // Assert
            assertEquals(role, actual)
        }
    }
}