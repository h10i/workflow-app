package com.example.workflow.feature.workflow.service.request

import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.core.workflow.request.RequestTypeRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class RequestTypeService(
    private val requestTypeRepository: RequestTypeRepository
) {
    @Transactional
    fun saveRequestType(requestType: RequestType): RequestType {
        return requestTypeRepository.save(requestType)
    }
}
