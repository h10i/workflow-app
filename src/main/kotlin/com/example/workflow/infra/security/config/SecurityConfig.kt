package com.example.workflow.infra.security.config

import com.example.workflow.common.constants.Role
import com.example.workflow.common.path.ApiPath
import com.example.workflow.infra.security.web.ProblemDetailAccessDeniedHandler
import com.example.workflow.infra.security.web.ProblemDetailAuthenticationEntryPoint
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
class SecurityConfig(
    private val problemDetailAuthenticationEntryPoint: ProblemDetailAuthenticationEntryPoint,
    private val problemDetailAccessDeniedHandler: ProblemDetailAccessDeniedHandler,
) {
    @Bean
    @Suppress("unused")
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                configureSpringDocAuthorizations()
                configureAccountAuthorizations()
                configureRoleAuthorizations()
                configureAuthAuthorizations()
                configureRequestTypeAuthorizations()
                authorize(anyRequest, denyAll)
            }
            oauth2ResourceServer {
                jwt {
                    authenticationEntryPoint = problemDetailAuthenticationEntryPoint
                    accessDeniedHandler = problemDetailAccessDeniedHandler
                }
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

    private fun AuthorizeHttpRequestsDsl.configureAuthAuthorizations() {
        authorize(
            HttpMethod.POST,
            "${ApiPath.Auth.BASE}${ApiPath.Auth.TOKEN}",
            permitAll,
        )
        authorize(
            HttpMethod.POST,
            "${ApiPath.Auth.BASE}${ApiPath.Auth.REFRESH_TOKEN}",
            permitAll,
        )
        authorize(
            HttpMethod.DELETE,
            "${ApiPath.Auth.BASE}${ApiPath.Auth.REVOKE}",
            authenticated,
        )
        authorize(
            HttpMethod.DELETE,
            "${ApiPath.Auth.BASE}${ApiPath.Auth.REVOKE_ALL}",
            authenticated,
        )
    }

    private fun AuthorizeHttpRequestsDsl.configureRequestTypeAuthorizations() {
        authorize(
            HttpMethod.POST,
            ApiPath.RequestType.BASE,
            authenticated,
        )
        authorize(
            HttpMethod.GET,
            "${ApiPath.RequestType.BASE}${ApiPath.RequestType.PATH_PATTERN_WITH_ID}",
            authenticated,
        )
        authorize(
            HttpMethod.GET,
            ApiPath.RequestType.BASE,
            authenticated,
        )
    }
}
