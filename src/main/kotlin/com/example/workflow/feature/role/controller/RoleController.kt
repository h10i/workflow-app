package com.example.workflow.feature.role.controller

import com.example.workflow.common.model.UnifiedErrorResponse
import com.example.workflow.common.path.ApiPath
import com.example.workflow.feature.role.model.CreateRoleRequest
import com.example.workflow.feature.role.model.RoleViewListResponse
import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.feature.role.presenter.CreateRolePresenter
import com.example.workflow.feature.role.presenter.GetAllRolesPresenter
import com.example.workflow.feature.role.usecase.CreateRoleUseCase
import com.example.workflow.feature.role.usecase.GetAllRolesUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiPath.Role.BASE)
class RoleController(
    private val createRoleUseCase: CreateRoleUseCase,
    private val createRolePresenter: CreateRolePresenter,
    private val getAllRolesUseCase: GetAllRolesUseCase,
    private val getAllRolesPresenter: GetAllRolesPresenter,
) {
    @Operation(
        summary = "Create a new role",
        description = "Creates a new role for the admin user.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Role Information",
            required = true,
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CreateRoleRequest::class)
                )
            ]
        ),
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Successfully created a role",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = RoleViewResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data or a general business validation error occurred. Details are provided in the 'errors' map.",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UnifiedErrorResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authentication credentials are missing or invalid.",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Required role missing.",
                content = [Content()]
            ),
        ],
    )
    @PostMapping
    fun createRole(@Valid @RequestBody request: CreateRoleRequest): ResponseEntity<RoleViewResponse> {
        val useCaseResult: CreateRoleUseCase.Result = createRoleUseCase.execute(request)
        val presenterResult: CreateRolePresenter.Result = createRolePresenter.toResponse(useCaseResult)
        return ResponseEntity.status(HttpStatus.CREATED).body(presenterResult.response)
    }

    @Operation(
        summary = "Get all roles",
        description = "Retrieves all roles for the admin user.",
        security = [SecurityRequirement(name = "bearer-key")],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved all roles information",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = RoleViewListResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authentication credentials are missing or invalid.",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Required role missing.",
                content = [Content()]
            ),
        ],
    )
    @GetMapping
    fun getAllRoles(): ResponseEntity<RoleViewListResponse> {
        val useCaseResult: GetAllRolesUseCase.Result = getAllRolesUseCase.execute()
        val presenterResult: GetAllRolesPresenter.Result = getAllRolesPresenter.toResponse(useCaseResult)
        return ResponseEntity.status(HttpStatus.OK).body(presenterResult.response)
    }
}