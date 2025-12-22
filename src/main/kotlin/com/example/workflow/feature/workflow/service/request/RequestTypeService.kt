package com.example.workflow.feature.workflow.service.request

import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.core.workflow.request.RequestTypeRepository
import com.example.workflow.feature.workflow.exception.request.RequestTypeNotFoundException
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
    fun findRequestTypeById(id: UUID): RequestType? {
        return requestTypeRepository.findById(id).orElse(null)
    }

    @Transactional
    fun getRequestTypeById(id: UUID): RequestType {
        return findRequestTypeById(id) ?: throw RequestTypeNotFoundException(
            mapOf(RequestType::id.name to id.toString())
        )
    }

    @Transactional
    fun getAllRequestType(): List<RequestType> {
        return requestTypeRepository.findAll()
    }

    @Transactional
    fun deleteRequestTypeById(id: UUID) {
        requestTypeRepository.deleteById(id)
    }
}
