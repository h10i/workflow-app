package com.example.workflow.unit.feature.workflow.usecase.request

import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.core.workflow.request.toViewDto
import com.example.workflow.feature.workflow.exception.request.RequestTypeNotFoundException
import com.example.workflow.feature.workflow.model.request.RequestTypeViewDto
import com.example.workflow.feature.workflow.service.request.RequestTypeService
import com.example.workflow.feature.workflow.usecase.request.GetRequestTypeUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class GetRequestTypeUseCaseTest {
    private lateinit var requestTypeService: RequestTypeService
    private lateinit var getRequestTypeUseCase: GetRequestTypeUseCase

    @BeforeEach
    fun setUp() {
        requestTypeService = mockk()
        getRequestTypeUseCase = GetRequestTypeUseCase(
            requestTypeService = requestTypeService,
        )
    }

    @Nested
    inner class ExecuteFun {
        @BeforeEach
        fun setUp() {
            mockkStatic(RequestType::toViewDto)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(RequestType::toViewDto)
        }

        @Test
        fun `should return UseCase result of a request type when a request type exists`() {
            // Arrange
            val requestTypeId = UUID.randomUUID()
            val requestType: RequestType = mockk()
            val requestTypeViewDto: RequestTypeViewDto = mockk()

            every { requestTypeService.getRequestTypeById(requestTypeId) } returns requestType
            every { requestType.toViewDto() } returns requestTypeViewDto

            // Act
            val actual = getRequestTypeUseCase.execute(requestTypeId)

            // Assert
            assertEquals(requestTypeViewDto, actual.requestTypeViewDto)
        }

        @Test
        fun `should throw RequestTypeNotFoundException when a request type does not exist`() {
            // Arrange
            val requestTypeId = UUID.randomUUID()
            every { requestTypeService.getRequestTypeById(requestTypeId) } returns null

            // Act
            val actual = assertThrows<RequestTypeNotFoundException> {
                getRequestTypeUseCase.execute(requestTypeId)
            }

            // Assert
            assertEquals("Request type not found (id: $requestTypeId)", actual.message)
        }
    }
}
