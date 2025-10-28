package com.example.workflow.unit.feature.workflow.usecase.request

import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.core.workflow.request.toViewDto
import com.example.workflow.feature.workflow.model.request.RequestTypeViewDto
import com.example.workflow.feature.workflow.service.request.RequestTypeService
import com.example.workflow.feature.workflow.usecase.request.GetAllRequestTypesUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@UnitTest
class GetAllRequestTypesUseCaseTest {
    private lateinit var requestTypeService: RequestTypeService
    private lateinit var getAllRequestTypesUseCase: GetAllRequestTypesUseCase

    @BeforeEach
    fun setUp() {
        requestTypeService = mockk()
        getAllRequestTypesUseCase = GetAllRequestTypesUseCase(
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
            val requestTypes: List<RequestType> = listOf(mockk(), mockk())
            val requestTypeViewDtoList: List<RequestTypeViewDto> = listOf(mockk(), mockk())

            every { requestTypeService.getAllRequestType() } returns requestTypes
            for (i in 0 until 2) {
                every { requestTypes[i].toViewDto() } returns requestTypeViewDtoList[i]
            }

            // Act
            val actual = getAllRequestTypesUseCase.execute()

            // Assert
            for (i in 0 until 2) {
                assertEquals(requestTypeViewDtoList[i], actual.requestTypeViewDtoList[i])
            }
        }

        @Test
        fun `should return UseCase result of an empty list when no request types exist`() {
            // Arrange
            val requestTypes: List<RequestType> = listOf()

            every { requestTypeService.getAllRequestType() } returns requestTypes

            // Act
            val actual = getAllRequestTypesUseCase.execute()

            // Assert
            assertTrue(actual.requestTypeViewDtoList.isEmpty())
        }
    }
}
