package com.example.workflow.feature.role.model

import io.swagger.v3.oas.annotations.media.Schema

data class RoleViewListResponse(
    @get:Schema(
        description = "role list",
        example = """
            "roles": [
                {
                    "id": "58196e4f-62cf-4a89-817e-d54116a78a43",
                    "name": "EXAMPLE_USER",
                },
                {
                    "id": "9bffdd2e-0f25-4032-ab31-ab390192e283",
                    "name": "EXAMPLE_ADMIN",
                }
            ]
        """,
    )
    val roles: List<RoleViewResponse>
)