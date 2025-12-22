package com.example.workflow.feature.workflow.usecase.request

import com.example.workflow.feature.workflow.service.request.RequestTypeService
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeleteRequestTypeUseCase(
    private val requestTypeService: RequestTypeService
) {
    fun execute(id: UUID) {
        requestTypeService.deleteRequestTypeById(id)
    }
}
