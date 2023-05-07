package com.example.socialapplication.data.remote.dto

import com.example.socialapplication.domain.model.Message
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@kotlinx.serialization.Serializable
data class MessageDto(
    val text: String = "",
    val timestamp: Long = 0,
    val username: String = "",
    val senderAvatar : String = "",
    val id: String = ""
) {
    fun toMessage(): Message {
        val data = Date(timestamp)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data)
        return Message(
            text = text,
            formattedTime = formattedDate,
            username = username
        )
    }
}