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
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

    @Nested
    inner class FindByName {
        private lateinit var role: Role

        @BeforeEach
        fun setUp() {
            // Arrange
            role = TestDataFactory.createRole()
            entityManager.persist(role)

            entityManager.flush()
            entityManager.clear()
        }

        @AfterEach
        fun tearDown() {
        }

        @Test
        fun `returns role when role name exists`() {
            // Arrange
            val roleName = role.name

            // Act
            val actual: Role? = roleRepository.findByName(roleName)

            // Assert
            assertEquals(role, actual)
        }

        @Test
        fun `returns role when role name doesn't exists`() {
            // Arrange
            val roleName = "NOT_FOUND_${role.name}"

            // Act
            val actual: Role? = roleRepository.findByName(roleName)

            // Assert
            assertNull(actual)
        }
    }

    @Nested
    inner class FindAll {
        @Test
        fun `returns empty list when role does not exist`() {
            // Arrange

            // Act
            val actual: List<Role> = roleRepository.findAll()

            // Assert
            assertTrue(actual.isEmpty())
        }

        @Test
        fun `returns roles when two roles exists`() {
            // Arrange
            val roles: List<Role> = listOf(
                TestDataFactory.createRole(name = "EXAMPLE1"),
                TestDataFactory.createRole(name = "EXAMPLE2"),
            )
            roles.forEach { entityManager.persist(it) }
            entityManager.flush()
            entityManager.clear()

            // Act
            val actual: List<Role> = roleRepository.findAll()

            // Assert
            assertEquals(roles.sortedBy { it.id }, actual.sortedBy { it.id })
        }
    }
}