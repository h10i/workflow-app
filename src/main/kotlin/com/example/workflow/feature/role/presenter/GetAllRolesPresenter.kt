package com.example.workflow.feature.role.presenter

import com.example.workflow.feature.role.model.RoleViewListResponse
import com.example.workflow.feature.role.model.toViewResponse
import com.example.workflow.feature.role.usecase.GetAllRolesUseCase
import org.springframework.stereotype.Component

@Component
class GetAllRolesPresenter {
    data class Result(
        val response: RoleViewListResponse
    )

    fun toResponse(useCaseResult: GetAllRolesUseCase.Result): Result {
        val roleViewResponseList = RoleViewListResponse(
            roles = useCaseResult.roleViewDtoList.map { it.toViewResponse() }
        )
        return Result(
            response = roleViewResponseList
        )
    }
}