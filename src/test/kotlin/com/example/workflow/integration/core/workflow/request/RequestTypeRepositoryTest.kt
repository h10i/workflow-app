package com.example.workflow.integration.core.workflow.request

import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.core.workflow.request.RequestTypeRepository
import com.example.workflow.support.annotation.CustomDataJpaTest
import com.example.workflow.support.annotation.IntegrationTest
import com.example.workflow.support.util.TestDataFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.util.*
import kotlin.test.assertTrue

@IntegrationTest
@CustomDataJpaTest
class RequestTypeRepositoryTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var requestTypeRepository: RequestTypeRepository

    @Nested
    inner class SaveFun {
        @Test
        fun `should return the saved request type when saving a new request type`() {
            // Arrange
            val requestType = TestDataFactory.createRequestType()

            // Act
            val actual: RequestType = requestTypeRepository.save(requestType)

            // Assert
            assertEquals(requestType, actual)
        }
    }

    @Nested
    inner class FindByIdFun {
        private lateinit var requestType: RequestType

        @BeforeEach
        fun setUp() {
            // Arrange
            requestType = TestDataFactory.createRequestType()
            entityManager.persist(requestType)

            entityManager.flush()
            entityManager.clear()
        }

        @Test
        fun `should return the request type when a request type id exists`() {
            // Arrange
            val requestTypeId = requestType.id

            // Act
            val actual: RequestType? = requestTypeRepository.findById(requestTypeId).orElse(null)

            // Assert
            assertEquals(requestType, actual)
        }

        @Test
        fun `should return null when a request type id does not exist`() {
            // Arrange
            val requestTypeId = UUID.randomUUID()

            // Act
            val actual: RequestType? = requestTypeRepository.findById(requestTypeId).orElse(null)

            // Assert
            assertNull(actual)
        }
    }

    @Nested
    inner class FindAllFun {
        @Test
        fun `should return empty list when a request type does not exist`() {
            // Arrange

            // Act
            val actual: List<RequestType> = requestTypeRepository.findAll()

            // Assert
            assertTrue(actual.isEmpty())
        }

        @Test
        fun `should return the request types when two request types exists`() {
            // Arrange
            val requestTypes: List<RequestType> = listOf(
                TestDataFactory.createRequestType(name = "request type name 1"),
                TestDataFactory.createRequestType(name = "request type name 2"),
            )
            requestTypes.forEach { entityManager.persist(it) }
            entityManager.flush()
            entityManager.clear()

            // Act
            val actual: List<RequestType> = requestTypeRepository.findAll()

            // Assert
            assertEquals(requestTypes.sortedBy { it.id }, actual.sortedBy { it.id })
        }
    }

    @Nested
    inner class DeleteByIdFun {
        private lateinit var requestType: RequestType

        @BeforeEach
        fun setUp() {
            requestType = TestDataFactory.createRequestType()
            entityManager.persist(requestType)

            entityManager.flush()
            entityManager.clear()
        }

        @Test
        fun `should delete a request type when a request type id exists`() {
            // Arrange

            // Act
            requestTypeRepository.deleteById(requestType.id)
            entityManager.flush()

            // Assert
            val actualRequestType = entityManager.find(RequestType::class.java, requestType.id)
            assertNull(actualRequestType)
        }
    }
}
