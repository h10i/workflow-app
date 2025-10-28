package com.example.workflow.unit.feature.workflow.controller.request

import com.example.workflow.feature.workflow.controller.request.RequestTypeController
import com.example.workflow.feature.workflow.model.request.CreateRequestTypeRequest
import com.example.workflow.feature.workflow.model.request.RequestTypeViewListResponse
import com.example.workflow.feature.workflow.model.request.RequestTypeViewResponse
import com.example.workflow.feature.workflow.presenter.request.RequestTypePresenter
import com.example.workflow.feature.workflow.usecase.request.CreateRequestTypeUseCase
import com.example.workflow.feature.workflow.usecase.request.GetAllRequestTypesUseCase
import com.example.workflow.feature.workflow.usecase.request.GetRequestTypeUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class RequestTypeControllerTest {
    private lateinit var requestTypePresenter: RequestTypePresenter
    private lateinit var createRequestTypeUseCase: CreateRequestTypeUseCase
    private lateinit var getRequestTypeUseCase: GetRequestTypeUseCase
    private lateinit var getAllRequestTypesUseCase: GetAllRequestTypesUseCase
    private lateinit var requestTypeController: RequestTypeController

    @BeforeEach
    fun setUp() {
        requestTypePresenter = mockk()
        createRequestTypeUseCase = mockk()
        getRequestTypeUseCase = mockk()
        getAllRequestTypesUseCase = mockk()
        requestTypeController = RequestTypeController(
            requestTypePresenter = requestTypePresenter,
            createRequestTypeUseCase = createRequestTypeUseCase,
            getRequestTypeUseCase = getRequestTypeUseCase,
            getAllRequestTypesUseCase = getAllRequestTypesUseCase,
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

    @Nested
    inner class GetRequestTypeFun {
        @Test
        fun `should execute GetRequestTypeUseCase and return request type view response`() {
            // Arrange
            val id = UUID.randomUUID()
            val useCaseResult: GetRequestTypeUseCase.Result = mockk(relaxed = true)
            val presenterResult: RequestTypePresenter.Result<RequestTypeViewResponse> = mockk()
            val response: RequestTypeViewResponse = mockk()

            every { getRequestTypeUseCase.execute(id) } returns useCaseResult
            every { requestTypePresenter.toResponse(useCaseResult.requestTypeViewDto) } returns presenterResult
            every { presenterResult.response } returns response

            // Act
            val actual = requestTypeController.getRequestType(id)

            // Assert
            assertEquals(HttpStatus.OK, actual.statusCode)
            assertEquals(response, actual.body)
        }
    }

    @Nested
    inner class GetAllRequestTypesFun {
        @Test
        fun `should execute GetAllRequestTypesUseCase and return request type view list response`() {
            // Arrange
            val useCaseResult: GetAllRequestTypesUseCase.Result = mockk(relaxed = true)
            val presenterResult: RequestTypePresenter.Result<RequestTypeViewListResponse> = mockk()
            val response: RequestTypeViewListResponse = mockk()

            every { getAllRequestTypesUseCase.execute() } returns useCaseResult
            every { requestTypePresenter.toResponse(useCaseResult.requestTypeViewDtoList) } returns presenterResult
            every { presenterResult.response } returns response

            // Act
            val actual = requestTypeController.getAllRequestTypes()

            // Assert
            assertEquals(HttpStatus.OK, actual.statusCode)
            assertEquals(response, actual.body)
        }
    }
}
