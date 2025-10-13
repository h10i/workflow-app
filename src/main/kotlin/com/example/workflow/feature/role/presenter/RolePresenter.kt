package com.example.workflow.feature.role.presenter

import com.example.workflow.feature.role.model.RoleViewDto
import com.example.workflow.feature.role.model.RoleViewListResponse
import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.feature.role.model.toViewResponse
import org.springframework.stereotype.Component

@Component
class RolePresenter {
    data class Result<T>(
        val response: T
    )

    fun toResponse(roleViewDto: RoleViewDto): Result<RoleViewResponse> {
        return Result(
            response = roleViewDto.toViewResponse()
        )
    }

    fun toResponse(roleViewDtoList: List<RoleViewDto>): Result<RoleViewListResponse> {
        return Result(
            response = RoleViewListResponse(
                roles = roleViewDtoList.map { it.toViewResponse() }
            )
        )
    }
}
