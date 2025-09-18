package com.example.workflow.unit.infra.security.config

import com.example.workflow.infra.security.config.SecurityComponentsConfig
import com.example.workflow.infra.security.model.RsaKeyProperties
import com.example.workflow.support.annotation.UnitTest
import com.example.workflow.support.util.TestDataFactory
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@UnitTest
class SecurityComponentsConfigTest {
    private lateinit var rsaKeyProperties: RsaKeyProperties
    private lateinit var securityConfig: SecurityComponentsConfig

    @BeforeEach
    fun setUp() {
        rsaKeyProperties = TestDataFactory.createRsaKeyProperties()
        securityConfig = SecurityComponentsConfig(rsaKeyProperties)
    }

    @Nested
    inner class PasswordEncoderFun {
        @Test
        fun `passwordEncoder returns BCryptPasswordEncoder`() {
            // Arrange

            // Act
            val encoder = securityConfig.passwordEncoder()

            // Assert
            assertTrue(encoder is BCryptPasswordEncoder)
        }
    }

    @Nested
    inner class JwtDecoderFun {
        @Test
        fun `jwtDecoder returns NimbusJwtDecoder`() {
            // Arrange

            // Act
            val decoder = securityConfig.jwtDecoder()

            // Assert
            assertNotNull(decoder)
        }
    }

    @Nested
    inner class JwtEncoderFun {
        @Test
        fun `jwtEncoder returns NimbusJwtEncoder`() {
            // Arrange

            // Act
            val encoder = securityConfig.jwtEncoder()

            // Assert
            assertNotNull(encoder)
        }
    }

    @Nested
    inner class JwtAuthenticationConverterFun {
        @Test
        fun `jwtAuthenticationConverter returns JwtAuthenticationConverter`() {
            // Arrange

            // Act
            val converter = securityConfig.jwtAuthenticationConverter()

            // Assert
            assertNotNull(converter)
        }
    }

    @Nested
    inner class AuthenticationManagerFun {
        @Test
        fun `authenticationManager returns configured ProviderManager`() {
            // Arrange
            val userDetailsService: UserDetailsService = mockk()
            val passwordEncoder: PasswordEncoder = mockk()

            // Act
            val authManager = securityConfig.authenticationManager(userDetailsService, passwordEncoder)

            // Assert
            assertTrue(authManager is ProviderManager)
        }
    }
}
