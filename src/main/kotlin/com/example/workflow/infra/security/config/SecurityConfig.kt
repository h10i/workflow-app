package com.example.workflow.infra.security.config

import com.example.workflow.common.path.ApiPath
import com.example.workflow.infra.security.model.RsaKeyProperties
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(RsaKeyProperties::class)
class SecurityConfig(private val rsaKeyProperties: RsaKeyProperties) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                // SpringDoc
                authorize(ApiPath.SpringDoc.API_DOCS_ALL, permitAll)
                authorize(ApiPath.SpringDoc.SWAGGER_UI_HTML, permitAll)
                authorize(ApiPath.SpringDoc.SWAGGER_UI_ALL, permitAll)
                // Account
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
                // Token
                authorize(
                    HttpMethod.POST,
                    "${ApiPath.Token.BASE}${ApiPath.Token.TOKEN}",
                    permitAll,
                )
                // Refresh Token
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
                // Others
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

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.publicKey).build()
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk: JWK = RSAKey.Builder(rsaKeyProperties.publicKey).privateKey(rsaKeyProperties.privateKey).build()
        return NimbusJwtEncoder(ImmutableJWKSet(JWKSet(jwk)))
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
        return jwtAuthenticationConverter
    }

    @Suppress("UsePropertyAccessSyntax")
    @Bean
    fun authenticationManager(
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder,
    ): AuthenticationManager {
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService)
        authenticationProvider.setPasswordEncoder(passwordEncoder)
        return ProviderManager(authenticationProvider)
    }
}