package com.example.socialapplication.data.remote.service

import com.example.socialapplication.domain.model.Message

interface MessageService {

    suspend fun getAllMessage() : List<Message>

    companion object {
        private const val HOST = "47.108.253.91"
        private const val PORT = "8008"
        const val BASE_URL = "http://$HOST:$PORT"

    }

    sealed class Endpoints(val url: String) {
        object GetAllMessages: Endpoints("$BASE_URL/messages")
    }
}