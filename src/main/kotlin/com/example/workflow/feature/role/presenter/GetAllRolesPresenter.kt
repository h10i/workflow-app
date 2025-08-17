package com.example.workflow.feature.role.presenter

import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.feature.role.model.toViewResponse
import com.example.workflow.feature.role.usecase.GetAllRolesUseCase
import org.springframework.stereotype.Component

@Component
class GetAllRolesPresenter {
    data class Result(
        val response: List<RoleViewResponse>
    )

    fun toResponse(useCaseResult: GetAllRolesUseCase.Result): Result {
        val roleViewResponseList: List<RoleViewResponse> = useCaseResult.roleViewDtoList.map { it.toViewResponse() }
        return Result(
            response = roleViewResponseList
        )
    }
}