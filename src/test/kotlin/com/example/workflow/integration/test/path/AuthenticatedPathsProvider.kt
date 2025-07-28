package com.example.workflow.integration.test.path

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.springframework.http.HttpMethod
import java.util.stream.Stream

class AuthenticatedPathsProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments?>? = Stream.of(
        // Auth
        Arguments.of(HttpMethod.DELETE, "/v1/auth/revoke"),
        Arguments.of(HttpMethod.DELETE, "/v1/auth/revoke/all"),
        // Account
        Arguments.of(HttpMethod.GET, "/v1/accounts/me"),
    )
}
