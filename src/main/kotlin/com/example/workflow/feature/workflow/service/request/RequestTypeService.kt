package com.example.workflow.feature.workflow.service.request

import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.core.workflow.request.RequestTypeRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class RequestTypeService(
    private val requestTypeRepository: RequestTypeRepository
) {
    @Transactional
    fun saveRequestType(requestType: RequestType): RequestType {
        return requestTypeRepository.save(requestType)
    }

    @Transactional
    fun getRequestTypeById(id: UUID): RequestType? {
        return requestTypeRepository.findById(id).orElse(null)
    }

    @Transactional
    fun getAllRequestType(): List<RequestType> {
        return requestTypeRepository.findAll()
    }
}
