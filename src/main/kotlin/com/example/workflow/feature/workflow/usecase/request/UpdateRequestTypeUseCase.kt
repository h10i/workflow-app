package com.example.workflow.feature.workflow.usecase.request

import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.core.workflow.request.toViewDto
import com.example.workflow.feature.workflow.model.request.RequestTypeViewDto
import com.example.workflow.feature.workflow.model.request.UpdateRequestTypeRequest
import com.example.workflow.feature.workflow.service.request.RequestTypeService
import org.springframework.stereotype.Service
import java.util.*

@Service
class UpdateRequestTypeUseCase(
    private val requestTypeService: RequestTypeService,
) {
    data class Result(
        val requestTypeViewDto: RequestTypeViewDto,
    )

    fun execute(id: UUID, request: UpdateRequestTypeRequest): Result {
        val requestType: RequestType = requestTypeService.getRequestTypeById(id)
        requestType.apply {
            request.name?.let { name = it }
            request.description?.let { description = it }
        }

        val savedRequestType = requestTypeService.saveRequestType(requestType)

        return Result(
            requestTypeViewDto = savedRequestType.toViewDto()
        )
    }
}
