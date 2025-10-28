package com.example.workflow.feature.workflow.usecase.request

import com.example.workflow.core.workflow.request.toViewDto
import com.example.workflow.feature.workflow.model.request.RequestTypeViewDto
import com.example.workflow.feature.workflow.service.request.RequestTypeService
import org.springframework.stereotype.Service

@Service
class GetAllRequestTypesUseCase(
    private val requestTypeService: RequestTypeService
) {
    data class Result(
        val requestTypeViewDtoList: List<RequestTypeViewDto>
    )

    fun execute(): Result {
        val requestTypes = requestTypeService.getAllRequestType()
        val requestTypeViewDtoList: List<RequestTypeViewDto> = requestTypes.map { it.toViewDto() }
        return Result(
            requestTypeViewDtoList = requestTypeViewDtoList,
        )
    }
}
