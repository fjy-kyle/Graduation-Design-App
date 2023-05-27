package com.example.socialapplication.presentation.home.chat

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app.presentation.chat.ChatState
import com.example.socialapplication.data.remote.service.ChatSocketService
import com.example.socialapplication.data.remote.service.MessageService
import com.example.socialapplication.main.SocialApp
import com.example.socialapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _messageText = mutableStateOf("")
    val messageText = _messageText

    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    private val _senderState = mutableStateMapOf<Int,ByteArray>()
    val senderState : SnapshotStateMap<Int,ByteArray> = _senderState



    // 建立websocket长连接
    fun connectToChat() {
        getAllMessages()
        val username = SocialApp.sharedPreferences.getString("username","")
        username?.let { _ ->
            viewModelScope.launch {
                when(val result = chatSocketService.initSession(username)) {
                    is Resource.Success -> {
                        chatSocketService.observeMessage()
                            .onEach { message ->
                                val newList = state.value.messages.toMutableList().apply {
                                    add(0, message)
                                }
                                _state.value = state.value.copy(
                                    messages = newList
                                )
                            }.launchIn(viewModelScope)
                    }
                    is Resource.Error -> {
                        _toastEvent.emit(result.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    fun disconnect(){
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    // 获得所有消息
    private fun getAllMessages(){
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            val result = messageService.getAllMessage()
            _state.value = state.value.copy(
                messages = result,
                isLoading = false
            )
        }
    }

    // 发送消息
    fun sendMessage() {
        viewModelScope.launch {
            if (messageText.value.isNotBlank()) {
                chatSocketService.sendMessage(messageText.value)
                _messageText.value = "" //发送后清空输入框
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}