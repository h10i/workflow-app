package com.example.workflow.feature.workflow.presenter.request

import com.example.workflow.feature.workflow.model.request.RequestTypeViewDto
import com.example.workflow.feature.workflow.model.request.RequestTypeViewListResponse
import com.example.workflow.feature.workflow.model.request.RequestTypeViewResponse
import com.example.workflow.feature.workflow.model.request.toViewResponse
import org.springframework.stereotype.Component

@Component
class RequestTypePresenter {
    data class Result<T>(
        val response: T
    )

    fun toResponse(requestTypeViewDto: RequestTypeViewDto): Result<RequestTypeViewResponse> {
        return Result(
            response = requestTypeViewDto.toViewResponse()
        )
    }

    fun toResponse(requestTypeViewDtoList: List<RequestTypeViewDto>): Result<RequestTypeViewListResponse> {
        return Result(
            response = RequestTypeViewListResponse(
                requestTypes = requestTypeViewDtoList.map { it.toViewResponse() }
            )
        )
    }
}
