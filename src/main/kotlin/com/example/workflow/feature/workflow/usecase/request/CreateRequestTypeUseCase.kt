package com.example.workflow.feature.workflow.usecase.request

import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.core.workflow.request.toViewDto
import com.example.workflow.feature.workflow.model.request.CreateRequestTypeRequest
import com.example.workflow.feature.workflow.model.request.RequestTypeViewDto
import com.example.workflow.feature.workflow.service.request.RequestTypeService
import org.springframework.stereotype.Service

@Service
class CreateRequestTypeUseCase(
    private val requestTypeService: RequestTypeService,
) {
    data class Result(
        val requestTypeViewDto: RequestTypeViewDto
    )

    fun execute(request: CreateRequestTypeRequest): Result {
        val requestType = RequestType(
            name = request.name,
            description = request.description,
            schemaDefinition = request.schemaDefinition,
        )
        val savedRequestType = requestTypeService.saveRequestType(requestType)
        return Result(
            requestTypeViewDto = savedRequestType.toViewDto(),
        )
    }
}
