package com.example.socialapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String = "",
    val text: String = "",
    val formattedTime: String = "",
    val username: String = "",
    val senderAvatar : String = ""
)