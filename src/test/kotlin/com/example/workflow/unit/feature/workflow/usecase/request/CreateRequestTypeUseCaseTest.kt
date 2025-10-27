package com.example.workflow.unit.feature.workflow.usecase.request

import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.core.workflow.request.toViewDto
import com.example.workflow.feature.workflow.model.request.CreateRequestTypeRequest
import com.example.workflow.feature.workflow.model.request.RequestTypeViewDto
import com.example.workflow.feature.workflow.service.request.RequestTypeService
import com.example.workflow.feature.workflow.usecase.request.CreateRequestTypeUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@UnitTest
class CreateRequestTypeUseCaseTest {
    private lateinit var requestTypeService: RequestTypeService
    private lateinit var createRequestTypeUseCase: CreateRequestTypeUseCase

    @BeforeEach
    fun setUp() {
        requestTypeService = mockk()
        createRequestTypeUseCase = CreateRequestTypeUseCase(requestTypeService)
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
        fun `should return the request type when valid request`() {
            // Arrange
            val request = CreateRequestTypeRequest(
                name = "example name",
                description = "example description",
                schemaDefinition = mockk(),
            )
            val savedRequestType: RequestType = mockk()
            val claimsSet = slot<RequestType>()
            every { requestTypeService.saveRequestType(capture(claimsSet)) } returns savedRequestType

            val requestTypeViewDto: RequestTypeViewDto = mockk()
            every { savedRequestType.toViewDto() } returns requestTypeViewDto

            // Act
            val actual = createRequestTypeUseCase.execute(request)

            // Assert
            val capturedRequestType = claimsSet.captured
            assertEquals(request.name, capturedRequestType.name)
            assertEquals(request.description, capturedRequestType.description)
            assertEquals(request.schemaDefinition, capturedRequestType.schemaDefinition)

            assertEquals(requestTypeViewDto, actual.requestTypeViewDto)
        }
    }
}
