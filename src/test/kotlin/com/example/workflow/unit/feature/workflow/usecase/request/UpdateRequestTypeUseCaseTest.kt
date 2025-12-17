package com.example.workflow.unit.feature.workflow.usecase.request

import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.core.workflow.request.toViewDto
import com.example.workflow.feature.workflow.model.request.RequestTypeViewDto
import com.example.workflow.feature.workflow.model.request.UpdateRequestTypeRequest
import com.example.workflow.feature.workflow.service.request.RequestTypeService
import com.example.workflow.feature.workflow.usecase.request.UpdateRequestTypeUseCase
import com.example.workflow.support.annotation.UnitTest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class UpdateRequestTypeUseCaseTest {
    private lateinit var requestTypeService: RequestTypeService
    private lateinit var updateRequestTypeUseCase: UpdateRequestTypeUseCase

    @BeforeEach
    fun setUp() {
        requestTypeService = mockk()
        updateRequestTypeUseCase = UpdateRequestTypeUseCase(
            requestTypeService = requestTypeService,
        )
    }

    @Nested
    inner class ExecuteFun {
        private val originalRequestTypeId = UUID.randomUUID()
        private val originalName = "original name"
        private val originalDescription = "original description"
        private val originalSchemaDefinition = jacksonObjectMapper().readTree("""{"foo":"original"}""")

        @BeforeEach
        fun setUp() {
            mockkStatic(RequestType::toViewDto)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(RequestType::toViewDto)
        }

        @Test
        fun `should not update any fields when request has no values`() {
            // Arrange

            // Act
            // Assert
            withUpdatedRequestTypeTest(
                id = originalRequestTypeId,
                request = UpdateRequestTypeRequest(),
                assert = { updated ->
                    assertEquals(originalName, updated.name)
                    assertEquals(originalDescription, updated.description)
                    assertEquals(originalSchemaDefinition, updated.schemaDefinition)
                }
            )
        }

        @Test
        fun `should update name when name is provided`() {
            // Arrange
            val newName = "new name"

            // Act
            // Assert
            withUpdatedRequestTypeTest(
                id = originalRequestTypeId,
                request = UpdateRequestTypeRequest(name = newName),
                assert = { updated ->
                    assertEquals(newName, updated.name)
                    assertEquals(originalDescription, updated.description)
                    assertEquals(originalSchemaDefinition, updated.schemaDefinition)
                }
            )
        }

        @Test
        fun `should update description when description is provided`() {
            // Arrange
            val newDescription = "new description"

            // Act
            // Assert
            withUpdatedRequestTypeTest(
                id = originalRequestTypeId,
                request = UpdateRequestTypeRequest(description = newDescription),
                assert = { updated ->
                    assertEquals(originalName, updated.name)
                    assertEquals(newDescription, updated.description)
                    assertEquals(originalSchemaDefinition, updated.schemaDefinition)
                }
            )
        }

        private fun withUpdatedRequestTypeTest(
            id: UUID,
            request: UpdateRequestTypeRequest,
            setup: (RequestType) -> Unit = {},
            mockAdditional: () -> Unit = {},
            assert: (RequestType) -> Unit,
        ) {
            // Arrange
            val originalRequestType = RequestType(
                id = originalRequestTypeId,
                name = originalName,
                description = originalDescription,
                schemaDefinition = originalSchemaDefinition,
            )

            setup(originalRequestType)

            val savedRequestType: RequestType = mockk()
            val requestTypeViewDto: RequestTypeViewDto = mockk()

            val capturedRequestType = slot<RequestType>()

            every { requestTypeService.getRequestTypeById(originalRequestTypeId) } returns originalRequestType
            every { requestTypeService.saveRequestType(capture(capturedRequestType)) } returns savedRequestType
            every { savedRequestType.toViewDto() } returns requestTypeViewDto

            mockAdditional()

            // Act
            val result = updateRequestTypeUseCase.execute(
                id = id,
                request = request,
            )

            // Assert
            assert(capturedRequestType.captured)
            assertEquals(requestTypeViewDto, result.requestTypeViewDto)
        }
    }
}
