package com.example.workflow.unit.feature.workflow.service.request

import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.core.workflow.request.RequestTypeRepository
import com.example.workflow.feature.workflow.service.request.RequestTypeService
import com.example.workflow.support.annotation.UnitTest
import com.example.workflow.support.util.TestDataFactory
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertNull

@UnitTest
class RequestTypeServiceTest {
    private lateinit var requestTypeRepository: RequestTypeRepository
    private lateinit var requestTypeService: RequestTypeService

    @BeforeEach
    fun setUp() {
        requestTypeRepository = mockk()
        requestTypeService = RequestTypeService(requestTypeRepository)
    }

    @Nested
    inner class SaveRequestTypeFun {
        @Test
        fun `should return the request type when saving a new request type`() {
            // Arrange
            val requestType: RequestType = mockk()
            val savedRequestType: RequestType = mockk()

            every { requestTypeRepository.save(requestType) } returns savedRequestType

            // Act
            val actual: RequestType = requestTypeService.saveRequestType(requestType)

            // Assert
            assertEquals(savedRequestType, actual)
        }
    }

    @Nested
    inner class GetRequestTypeByIdFun {
        @Test
        fun `should return the request type when a role exists`() {
            // Arrange
            val requestTypeId = UUID.randomUUID()
            val requestType = TestDataFactory.createRequestType(id = requestTypeId)

            every { requestTypeRepository.findById(requestTypeId) } returns Optional.of(requestType)

            // Act
            val actual: RequestType? = requestTypeService.getRequestTypeById(requestTypeId)

            // Assert
            assertEquals(requestType, actual)
        }

        @Test
        fun `should return null when a role does not exists`() {
            // Arrange
            val requestTypeId = UUID.randomUUID()

            every { requestTypeRepository.findById(requestTypeId) } returns Optional.empty()

            // Act
            val actual: RequestType? = requestTypeService.getRequestTypeById(requestTypeId)

            // Assert
            assertNull(actual)
        }
    }
}
