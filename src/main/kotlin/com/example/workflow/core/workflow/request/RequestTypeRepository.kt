package com.example.workflow.core.workflow.request

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RequestTypeRepository : JpaRepository<RequestType, UUID>
