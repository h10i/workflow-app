package com.example.workflow.feature.workflow.model.request

import io.swagger.v3.oas.annotations.media.Schema

data class RequestTypeViewListResponse(
    @get:Schema(
        description = "request type list",
        example = """
            "requestTypes": [
                {
                    "id": "7a98fef2-7993-43c7-9101-45bbf743a93d",
                    "name": "Request type name",
                    "description": "Description of the request type",
                    "schemaDefinition": {
                        "type": "object",
                        "required": [
                            "amount"
                        ],
                        "properties": {
                            ...
                        }
                    }
                },
                {
                    "id": ...,
                    "name": ...,
                    "description": ...,
                    "schemaDefinition": {
                        ...
                    }
                },
                ...
            ]
        """
    )
    val requestTypes: List<RequestTypeViewResponse>
)
