package com.example.chat_app.presentation.chat


import com.example.socialapplication.domain.model.Message


data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)
