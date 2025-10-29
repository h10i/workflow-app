package com.example.workflow.feature.workflow.exception.request

import com.example.workflow.common.exception.ResourceNotFoundException

class RequestTypeNotFoundException(
    searchCriteria: Map<String, String>
) : ResourceNotFoundException(resourceName = "Request type", searchCriteria = searchCriteria)
