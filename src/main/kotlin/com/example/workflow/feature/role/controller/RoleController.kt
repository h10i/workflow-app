package com.example.workflow.feature.role.controller

import com.example.workflow.common.path.ApiPath
import com.example.workflow.feature.role.model.CreateRoleRequest
import com.example.workflow.feature.role.model.RoleViewListResponse
import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.feature.role.presenter.RolePresenter
import com.example.workflow.feature.role.usecase.CreateRoleUseCase
import com.example.workflow.feature.role.usecase.DeleteRoleUseCase
import com.example.workflow.feature.role.usecase.GetAllRolesUseCase
import com.example.workflow.feature.role.usecase.GetRoleUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping(ApiPath.Role.BASE)
class RoleController(
    private val rolePresenter: RolePresenter,
    private val createRoleUseCase: CreateRoleUseCase,
    private val getRoleUseCase: GetRoleUseCase,
    private val getAllRolesUseCase: GetAllRolesUseCase,
    private val deleteRoleUseCase: DeleteRoleUseCase,
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
            ApiResponse(
                responseCode = "403",
                description = "Required role missing.",
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
    fun createRole(@Valid @RequestBody request: CreateRoleRequest): ResponseEntity<RoleViewResponse> {
        val useCaseResult: CreateRoleUseCase.Result = createRoleUseCase.execute(request)
        val presenterResult: RolePresenter.Result<RoleViewResponse> =
            rolePresenter.toResponse(useCaseResult.roleViewDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(presenterResult.response)
    }

    @Operation(
        summary = "Get a role",
        description = "Retrieves a role. This operation requires an ADMIN role.",
        security = [SecurityRequirement(name = "bearer-key")],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved a role information",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = RoleViewResponse::class)
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
                responseCode = "403",
                description = "Required role missing.",
                content = [
                    Content(
                        mediaType = "application/problem+json",
                        schema = Schema(implementation = ProblemDetail::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "A role not found.",
                content = [
                    Content(
                        mediaType = "application/problem+json",
                        schema = Schema(implementation = ProblemDetail::class)
                    )
                ]
            ),
        ],
    )
    @GetMapping(ApiPath.Role.ID)
    fun getRole(@PathVariable id: UUID): ResponseEntity<RoleViewResponse> {
        val useCaseResult: GetRoleUseCase.Result = getRoleUseCase.execute(id)
        val presenterResult: RolePresenter.Result<RoleViewResponse> =
            rolePresenter.toResponse(useCaseResult.roleViewDto)
        return ResponseEntity.status(HttpStatus.OK).body(presenterResult.response)
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
                content = [
                    Content(
                        mediaType = "application/problem+json",
                        schema = Schema(implementation = ProblemDetail::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Required role missing.",
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
    fun getAllRoles(): ResponseEntity<RoleViewListResponse> {
        val useCaseResult: GetAllRolesUseCase.Result = getAllRolesUseCase.execute()
        val presenterResult: RolePresenter.Result<RoleViewListResponse> =
            rolePresenter.toResponse(useCaseResult.roleViewDtoList)
        return ResponseEntity.status(HttpStatus.OK).body(presenterResult.response)
    }

    @Operation(
        summary = "Delete a role",
        description = "Deletes a role. This operation requires an ADMIN role.",
        security = [SecurityRequirement(name = "bearer-key")],
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "Successfully deleted a role",
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
                content = [
                    Content(
                        mediaType = "application/problem+json",
                        schema = Schema(implementation = ProblemDetail::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Required role missing.",
                content = [
                    Content(
                        mediaType = "application/problem+json",
                        schema = Schema(implementation = ProblemDetail::class)
                    )
                ]
            ),
        ],
    )
    @DeleteMapping(ApiPath.Role.ID)
    fun deleteRole(@PathVariable id: UUID): ResponseEntity<Void> {
        deleteRoleUseCase.execute(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
