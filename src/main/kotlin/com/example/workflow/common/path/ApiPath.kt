package com.example.workflow.common.path

object ApiPath {
    object Account {
        const val BASE = "${ApiVersion.V1}/accounts"
        const val ME = "/me"
    }

    object RefreshToken {
        const val BASE = "${ApiVersion.V1}/auth"
        const val REFRESH_TOKEN = "/refresh-token"
        const val REVOKE = "/revoke"
        const val REVOKE_ALL = "${REVOKE}/all"
    }

    object Token {
        const val BASE = "${ApiVersion.V1}/auth"
        const val TOKEN = "/token"
    }
}