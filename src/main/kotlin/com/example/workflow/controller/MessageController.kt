package com.example.workflow.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CopyOnWriteArrayList

@RestController
class MessageController {
    @get:GetMapping("/messages")
    val messages: MutableList<Message?> = CopyOnWriteArrayList<Message?>()

    @PostMapping("/messages")
    fun postMessages(@RequestBody text: String?, @AuthenticationPrincipal jwt: Jwt): Message {
        val message = Message(text, jwt.subject)
        this.messages.add(message)
        return message
    }

    @JvmRecord
    data class Message(val text: String?, val username: String?)
}