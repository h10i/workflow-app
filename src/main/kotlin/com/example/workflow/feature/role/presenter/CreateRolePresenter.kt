package com.example.workflow.feature.role.presenter

import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.feature.role.model.toViewResponse
import com.example.workflow.feature.role.usecase.CreateRoleUseCase
import org.springframework.stereotype.Component

@Component
class CreateRolePresenter {
    data class Result(
        val response: RoleViewResponse
    )

    fun toResponse(useCaseResult: CreateRoleUseCase.Result): Result {
        return Result(
            useCaseResult.roleViewDto.toViewResponse()
        )
    }
}