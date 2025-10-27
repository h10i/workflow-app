package com.example.workflow.unit.feature.workflow.service.request

import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.core.workflow.request.RequestTypeRepository
import com.example.workflow.feature.workflow.service.request.RequestTypeService
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
}
