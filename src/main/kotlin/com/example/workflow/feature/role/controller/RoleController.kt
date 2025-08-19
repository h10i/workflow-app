package com.example.workflow.feature.role.controller

import com.example.workflow.common.path.ApiPath
import com.example.workflow.feature.role.model.CreateRoleRequest
import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.feature.role.presenter.CreateRolePresenter
import com.example.workflow.feature.role.presenter.GetAllRolesPresenter
import com.example.workflow.feature.role.usecase.CreateRoleUseCase
import com.example.workflow.feature.role.usecase.GetAllRolesUseCase
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
    @PostMapping
    fun createRole(@Valid @RequestBody request: CreateRoleRequest): ResponseEntity<RoleViewResponse> {
        val useCaseResult: CreateRoleUseCase.Result = createRoleUseCase.execute(request)
        val presenterResult: CreateRolePresenter.Result = createRolePresenter.toResponse(useCaseResult)
        return ResponseEntity.status(HttpStatus.CREATED).body(presenterResult.response)
    }

    @GetMapping
    fun getAllRoles(): ResponseEntity<List<RoleViewResponse>> {
        val useCaseResult: GetAllRolesUseCase.Result = getAllRolesUseCase.execute()
        val presenterResult: GetAllRolesPresenter.Result = getAllRolesPresenter.toResponse(useCaseResult)
        return ResponseEntity.status(HttpStatus.OK).body(presenterResult.response)
    }
}