package com.example.workflow.integration.core.role

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.AccountRole
import com.example.workflow.core.auth.RefreshToken
import com.example.workflow.core.role.Role
import com.example.workflow.core.role.RoleRepository
import com.example.workflow.support.annotation.CustomDataJpaTest
import com.example.workflow.support.annotation.IntegrationTest
import com.example.workflow.support.util.TestDataFactory
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@IntegrationTest
@CustomDataJpaTest
class RoleRepositoryTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Nested
    inner class SaveFun {
        @Test
        fun `should return the saved role when saving a new role`() {
            // Arrange
            val role = TestDataFactory.createRole()

            // Act
            val actual: Role = roleRepository.save(role)

            // Assert
            assertEquals(role, actual)
        }
    }

    @Nested
    inner class FindByIdFun {
        private lateinit var role: Role

        @BeforeEach
        fun setUp() {
            // Arrange
            role = TestDataFactory.createRole()
            entityManager.persist(role)

            entityManager.flush()
            entityManager.clear()
        }

        @Test
        fun `should return the role when a role id exists`() {
            // Arrange
            val roleId = role.id

            // Act
            val actual: Role? = roleRepository.findById(roleId).orElse(null)

            // Assert
            assertEquals(role, actual)
        }

        @Test
        fun `should return null when a role id does not exist`() {
            // Arrange
            val roleId = UUID.randomUUID()

            // Act
            val actual: Role? = roleRepository.findById(roleId).orElse(null)

            // Assert
            assertNull(actual)
        }
    }

    @Nested
    inner class FindByNameFun {
        private lateinit var role: Role

        @BeforeEach
        fun setUp() {
            // Arrange
            role = TestDataFactory.createRole()
            entityManager.persist(role)

            entityManager.flush()
            entityManager.clear()
        }

        @Test
        fun `should return the role when a role name exists`() {
            // Arrange
            val roleName = role.name

            // Act
            val actual: Role? = roleRepository.findByName(roleName)

            // Assert
            assertEquals(role, actual)
        }

        @Test
        fun `should return null when a role name does not exist`() {
            // Arrange
            val roleName = "NOT_FOUND_${role.name}"

            // Act
            val actual: Role? = roleRepository.findByName(roleName)

            // Assert
            assertNull(actual)
        }
    }

    @Nested
    inner class FindAllFun {
        @Test
        fun `should return empty list when a role does not exist`() {
            // Arrange

            // Act
            val actual: List<Role> = roleRepository.findAll()

            // Assert
            assertTrue(actual.isEmpty())
        }

        @Test
        fun `should return the roles when two roles exists`() {
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

    @Nested
    inner class DeleteByIdFun {
        private lateinit var role: Role
        private lateinit var account: Account
        private lateinit var accountRole: AccountRole
        private lateinit var refreshToken: RefreshToken

        @BeforeEach
        fun setUp() {
            // Arrange
            role = TestDataFactory.createRole(name = "EXAMPLE_ROLE")
            entityManager.persist(role)

            account = TestDataFactory.createAccount()
            entityManager.persist(account)

            accountRole = TestDataFactory.registerAccountRole(account = account, role = role)
            entityManager.persist(accountRole)

            refreshToken = TestDataFactory.registerRefreshToken(account = account)
            entityManager.persist(refreshToken)

            entityManager.flush()
            entityManager.clear()
        }

        @Test
        fun `should delete role-related data when an role is deleted`() {
            // Act

            // Act
            roleRepository.deleteById(role.id)
            entityManager.flush()

            // Assert
            val actualRole = entityManager.find(Role::class.java, role.id)
            assertNull(actualRole)
            val actualAccount = entityManager.find(Account::class.java, account.id)
            assertNotNull(actualAccount)
            val actualAccountRole = entityManager.find(AccountRole::class.java, accountRole.id)
            assertNull(actualAccountRole)
        }
    }
}
