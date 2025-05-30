package com.example.workflow.service

import com.example.workflow.model.Account
import com.example.workflow.model.AuthUser
import com.example.workflow.repository.AccountRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val accountRepository: AccountRepository
) : UserDetailsService {
    override fun loadUserByUsername(mailAddress: String): UserDetails {
        val account: Account = accountRepository.findByMailAddress(mailAddress)
            ?: throw UsernameNotFoundException("Account not found: $mailAddress")
        return AuthUser(account)
    }
}