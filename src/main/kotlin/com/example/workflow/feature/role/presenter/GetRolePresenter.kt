package com.example.workflow.feature.role.presenter

import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.feature.role.model.toViewResponse
import com.example.workflow.feature.role.usecase.GetRoleUseCase
import org.springframework.stereotype.Component

@Component
class GetRolePresenter {
    data class Result(
        val response: RoleViewResponse
    )

    fun toResponse(useCaseResult: GetRoleUseCase.Result): Result {
        return Result(
            response = useCaseResult.roleViewDto.toViewResponse()
        )
    }
}