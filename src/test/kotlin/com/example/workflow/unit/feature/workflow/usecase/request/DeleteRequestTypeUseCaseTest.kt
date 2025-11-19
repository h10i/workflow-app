package com.example.workflow.unit.feature.workflow.usecase.request

import com.example.workflow.feature.workflow.service.request.RequestTypeService
import com.example.workflow.feature.workflow.usecase.request.DeleteRequestTypeUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

@UnitTest
class DeleteRequestTypeUseCaseTest {
    private lateinit var requestTypeService: RequestTypeService
    private lateinit var deleteRequestTypeUseCase: DeleteRequestTypeUseCase

    @BeforeEach
    fun setUp() {
        requestTypeService = mockk()
        deleteRequestTypeUseCase = DeleteRequestTypeUseCase(
            requestTypeService = requestTypeService,
        )
    }

    @Nested
    inner class ExecuteFun {
        @Test
        fun `should delete the request type by id when a request type exists`() {
            // Arrange
            val requestTypeId = UUID.randomUUID()

            every { requestTypeService.deleteRequestTypeById(requestTypeId) } just runs

            // Act
            deleteRequestTypeUseCase.execute(requestTypeId)

            // Assert
            verify(exactly = 1) { requestTypeService.deleteRequestTypeById(requestTypeId) }
        }
    }
}
