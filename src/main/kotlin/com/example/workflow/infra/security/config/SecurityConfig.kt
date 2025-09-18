package com.example.workflow.infra.security.config

import com.example.workflow.common.constants.Role
import com.example.workflow.common.path.ApiPath
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.AuthorizeHttpRequestsDsl
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    @Suppress("unused")
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                configureSpringDocAuthorizations()
                configureAccountAuthorizations()
                configureRoleAuthorizations()
                configureTokenAuthorizations()
                configureRefreshTokenAuthorizations()
                authorize(anyRequest, denyAll)
            }
            oauth2ResourceServer {
                jwt {}
            }
            csrf { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
        }
        return http.build()
    }

    private fun AuthorizeHttpRequestsDsl.configureSpringDocAuthorizations() {
        authorize(ApiPath.SpringDoc.API_DOCS_ALL, permitAll)
        authorize(ApiPath.SpringDoc.SWAGGER_UI_HTML, permitAll)
        authorize(ApiPath.SpringDoc.SWAGGER_UI_ALL, permitAll)
    }

    private fun AuthorizeHttpRequestsDsl.configureAccountAuthorizations() {
        authorize(
            HttpMethod.GET,
            "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
            authenticated,
        )
        authorize(HttpMethod.POST, ApiPath.Account.BASE, permitAll)
        authorize(
            HttpMethod.PATCH,
            "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
            authenticated
        )
        authorize(
            HttpMethod.DELETE,
            "${ApiPath.Account.BASE}${ApiPath.Account.ME}",
            authenticated
        )
    }

    private fun AuthorizeHttpRequestsDsl.configureRoleAuthorizations() {
        authorize(
            HttpMethod.POST,
            ApiPath.Role.BASE,
            hasRole(Role.ADMIN.name)
        )
        authorize(
            HttpMethod.GET,
            "${ApiPath.Role.BASE}${ApiPath.Role.PATH_PATTERN_WITH_ID}",
            hasRole(Role.ADMIN.name)
        )
        authorize(HttpMethod.GET, ApiPath.Role.BASE, hasRole(Role.ADMIN.name))
        authorize(
            HttpMethod.DELETE,
            "${ApiPath.Role.BASE}${ApiPath.Role.PATH_PATTERN_WITH_ID}",
            hasRole(Role.ADMIN.name)
        )
    }

    private fun AuthorizeHttpRequestsDsl.configureTokenAuthorizations() {
        authorize(
            HttpMethod.POST,
            "${ApiPath.Token.BASE}${ApiPath.Token.TOKEN}",
            permitAll,
        )
    }

    private fun AuthorizeHttpRequestsDsl.configureRefreshTokenAuthorizations() {
        authorize(
            HttpMethod.POST,
            "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REFRESH_TOKEN}",
            permitAll,
        )
        authorize(
            HttpMethod.DELETE,
            "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE}",
            authenticated,
        )
        authorize(
            HttpMethod.DELETE,
            "${ApiPath.RefreshToken.BASE}${ApiPath.RefreshToken.REVOKE_ALL}",
            authenticated,
        )
    }
}
