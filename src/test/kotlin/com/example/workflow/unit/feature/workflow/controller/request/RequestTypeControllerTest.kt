package com.example.workflow.unit.feature.workflow.controller.request

import com.example.workflow.feature.workflow.controller.request.RequestTypeController
import com.example.workflow.feature.workflow.model.request.CreateRequestTypeRequest
import com.example.workflow.feature.workflow.model.request.RequestTypeViewResponse
import com.example.workflow.feature.workflow.presenter.request.RequestTypePresenter
import com.example.workflow.feature.workflow.usecase.request.CreateRequestTypeUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@UnitTest
class RequestTypeControllerTest {
    private lateinit var requestTypePresenter: RequestTypePresenter
    private lateinit var createRequestTypeUseCase: CreateRequestTypeUseCase
    private lateinit var requestTypeController: RequestTypeController

    @BeforeEach
    fun setUp() {
        requestTypePresenter = mockk()
        createRequestTypeUseCase = mockk()
        requestTypeController = RequestTypeController(
            requestTypePresenter = requestTypePresenter,
            createRequestTypeUseCase = createRequestTypeUseCase,
        )
    }

    @Nested
    inner class CreateRequestTypeFun {
        @Test
        fun `should execute CreateRequestTypeUseCase and return request type view response`() {
            // Arrange
            val request: CreateRequestTypeRequest = mockk()
            val useCaseResult: CreateRequestTypeUseCase.Result = mockk(relaxed = true)
            val requestTypeViewResponse: RequestTypeViewResponse = mockk()
            val presenterResult = RequestTypePresenter.Result(
                response = requestTypeViewResponse
            )

            every { createRequestTypeUseCase.execute(request) } returns useCaseResult
            every { requestTypePresenter.toResponse(useCaseResult.requestTypeViewDto) } returns presenterResult

            // Act
            val actual = requestTypeController.createRequestType(request)

            // Assert
            assertEquals(HttpStatus.CREATED, actual.statusCode)
            assertEquals(requestTypeViewResponse, actual.body)
        }
    }
}
