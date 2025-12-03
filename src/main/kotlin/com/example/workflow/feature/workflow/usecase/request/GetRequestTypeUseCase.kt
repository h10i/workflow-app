package com.example.workflow.feature.workflow.usecase.request

import com.example.workflow.core.workflow.request.toViewDto
import com.example.workflow.feature.workflow.model.request.RequestTypeViewDto
import com.example.workflow.feature.workflow.service.request.RequestTypeService
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetRequestTypeUseCase(
    private val requestTypeService: RequestTypeService
) {
    data class Result(
        val requestTypeViewDto: RequestTypeViewDto
    )

    fun execute(id: UUID): Result {
        val requestType = requestTypeService.getRequestTypeById(id)
        return Result(
            requestTypeViewDto = requestType.toViewDto()
        )
    }
}
