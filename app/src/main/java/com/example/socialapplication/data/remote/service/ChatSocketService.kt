package com.example.socialapplication.data.remote.service

import com.example.socialapplication.domain.model.Message
import com.example.socialapplication.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {


    suspend fun initSession(
        username: String
    ): Resource<Unit>

    suspend fun sendMessage(message: String)

    fun observeMessage(): Flow<Message>

    suspend fun closeSession()



    companion object {
        private const val HOST = "47.108.253.91"
        private const val PORT = "8008"
        const val BASE_URL = "ws://$HOST:$PORT"
    }

    sealed class EndPoints(val url: String) {
        object ChatSocket: EndPoints("$BASE_URL/chat-socket")
    }
}