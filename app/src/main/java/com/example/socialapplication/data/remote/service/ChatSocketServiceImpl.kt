package com.example.socialapplication.data.remote.service

import android.util.Log
import com.example.socialapplication.data.remote.dto.MessageDto
import com.example.socialapplication.domain.model.Message
import com.example.socialapplication.util.Resource
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.cio.websocket.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChatSocketServiceImpl(
    private val client: HttpClient
): ChatSocketService {

    private var socket: WebSocketSession? = null

    override suspend fun initSession(username: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url("${ChatSocketService.EndPoints.ChatSocket.url}?username=$username")
            }
            if (socket?.isActive == true) {
                Resource.Success(Unit)
            } else Resource.Error(message = "Couldn't establish a connection.")
        } catch (e: Exception) {
            e.printStack()
            Resource.Error(message = e.localizedMessage ?: "Unknown error")
        }
    }

    override suspend fun sendMessage(message: String) {
        try {
            socket?.send(Frame.Text(message))
        } catch (e:Exception) {
            e.printStack()
        }
    }


    override fun observeMessage(): Flow<Message> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val messageDto = Json.decodeFromString<MessageDto>(json)
                    messageDto.toMessage()
                } ?: flow { }
        } catch (e:Exception) {
            e.printStack()
            flow {  }
        }
    }

    override suspend fun downLoadSenderAvatar(avatar: String): ByteArray {
        Log.d("Here is a Error", avatar)
        val httpResponse: HttpResponse = client.get(avatar) {
            onDownload { bytesSentTotal, contentLength ->
                println("Received $bytesSentTotal bytes from $contentLength")
            }
        }
        return httpResponse.readBytes()
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}