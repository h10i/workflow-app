package com.example.workflow.feature.workflow.controller.request

import com.example.workflow.common.path.ApiPath
import com.example.workflow.feature.workflow.model.request.CreateRequestTypeRequest
import com.example.workflow.feature.workflow.model.request.RequestTypeViewResponse
import com.example.workflow.feature.workflow.presenter.request.RequestTypePresenter
import com.example.workflow.feature.workflow.usecase.request.CreateRequestTypeUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(ApiPath.RequestType.BASE)
class RequestTypeController(
    private val requestTypePresenter: RequestTypePresenter,
    private val createRequestTypeUseCase: CreateRequestTypeUseCase,
) {
    @Operation(
        summary = "Create a new request type",
        description = "Creates a new new request type.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Request type Information",
            required = true,
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CreateRequestTypeRequest::class)
                )
            ]
        ),
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Successfully created a request type",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = RequestTypeViewResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data or a general business validation error occurred." +
                    " Details are provided in the 'errors' map.",
                content = [
                    Content(
                        mediaType = "application/problem+json",
                        schema = Schema(implementation = ProblemDetail::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authentication credentials are missing or invalid.",
                content = [
                    Content(
                        mediaType = "application/problem+json",
                        schema = Schema(implementation = ProblemDetail::class)
                    )
                ]
            ),
        ],
    )
    @PostMapping
    fun createRequestType(
        @Valid @RequestBody request: CreateRequestTypeRequest
    ): ResponseEntity<RequestTypeViewResponse> {
        val useCaseResult = createRequestTypeUseCase.execute(request)
        val presenterResult = requestTypePresenter.toResponse(useCaseResult.requestTypeViewDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(presenterResult.response)
    }
}
