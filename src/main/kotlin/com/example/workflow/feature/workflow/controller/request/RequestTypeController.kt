package com.example.workflow.feature.workflow.controller.request

import com.example.workflow.common.path.ApiPath
import com.example.workflow.feature.workflow.model.request.CreateRequestTypeRequest
import com.example.workflow.feature.workflow.model.request.RequestTypeViewListResponse
import com.example.workflow.feature.workflow.model.request.RequestTypeViewResponse
import com.example.workflow.feature.workflow.presenter.request.RequestTypePresenter
import com.example.workflow.feature.workflow.usecase.request.CreateRequestTypeUseCase
import com.example.workflow.feature.workflow.usecase.request.DeleteRequestTypeUseCase
import com.example.workflow.feature.workflow.usecase.request.GetAllRequestTypesUseCase
import com.example.workflow.feature.workflow.usecase.request.GetRequestTypeUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

@Controller
@RequestMapping(ApiPath.RequestType.BASE)
class RequestTypeController(
    private val requestTypePresenter: RequestTypePresenter,
    private val createRequestTypeUseCase: CreateRequestTypeUseCase,
    private val getRequestTypeUseCase: GetRequestTypeUseCase,
    private val getAllRequestTypesUseCase: GetAllRequestTypesUseCase,
    private val deleteRequestTypeUseCase: DeleteRequestTypeUseCase,
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

    @Operation(
        summary = "Get a request type",
        description = "Retrieves a request type.",
        security = [SecurityRequirement(name = "bearer-key")],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved a request type information",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = RequestTypeViewResponse::class)
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
            ApiResponse(
                responseCode = "404",
                description = "A request type not found.",
                content = [
                    Content(
                        mediaType = "application/problem+json",
                        schema = Schema(implementation = ProblemDetail::class)
                    )
                ]
            ),
        ],
    )
    @GetMapping(ApiPath.RequestType.ID)
    fun getRequestType(@PathVariable id: UUID): ResponseEntity<RequestTypeViewResponse> {
        val useCaseResult = getRequestTypeUseCase.execute(id)
        val presenterResult = requestTypePresenter.toResponse(useCaseResult.requestTypeViewDto)
        return ResponseEntity.status(HttpStatus.OK).body(presenterResult.response)
    }

    @Operation(
        summary = "Get all request types",
        description = "Retrieves all request types.",
        security = [SecurityRequirement(name = "bearer-key")],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved all request types information",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = RequestTypeViewListResponse::class)
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
    @GetMapping
    fun getAllRequestTypes(): ResponseEntity<RequestTypeViewListResponse> {
        val useCaseResult = getAllRequestTypesUseCase.execute()
        val presenterResult = requestTypePresenter.toResponse(useCaseResult.requestTypeViewDtoList)
        return ResponseEntity.status(HttpStatus.OK).body(presenterResult.response)
    }

    @Operation(
        summary = "Delete a request type",
        description = "Deletes a request type.",
        security = [SecurityRequirement(name = "bearer-key")],
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "Successfully deleted a request type",
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
    @DeleteMapping(ApiPath.RequestType.ID)
    fun deleteRequestType(@PathVariable id: UUID): ResponseEntity<Void> {
        deleteRequestTypeUseCase.execute(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
