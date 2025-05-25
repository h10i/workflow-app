package com.example.workflow.service

import com.example.workflow.repository.AuthUserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val authUserRepository: AuthUserRepository
) : UserDetailsService {
    override fun loadUserByUsername(mailAddress: String): UserDetails {
        return authUserRepository.findByMailAddress(mailAddress)
            ?: throw UsernameNotFoundException("User not found: $mailAddress")
    }
}