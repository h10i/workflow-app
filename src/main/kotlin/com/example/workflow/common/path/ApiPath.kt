package com.example.workflow.common.path

object ApiPath {
    object SpringDoc {
        const val API_DOCS_ALL = "/v3/api-docs/**"
        const val SWAGGER_UI_HTML = "/swagger-ui.html"
        const val SWAGGER_UI_ALL = "/swagger-ui/**"
    }

    object Account {
        const val BASE = "${ApiVersion.V1}/accounts"
        const val ME = "/me"
    }

    object Auth {
        const val BASE = "${ApiVersion.V1}/auth"
        const val TOKEN = "/token"
        const val REFRESH_TOKEN = "/refresh-token"
        const val REVOKE = "/revoke"
        const val REVOKE_ALL = "$REVOKE/all"
    }

    object Role {
        const val BASE = "${ApiVersion.V1}/roles"
        const val ID = "/{id}"
        const val PATH_PATTERN_WITH_ID = "/*"
    }

    object RequestType {
        const val BASE = "${ApiVersion.V1}/request-types"
        const val ID = "/{id}"
        const val PATH_PATTERN_WITH_ID = "/*"
    }
}
