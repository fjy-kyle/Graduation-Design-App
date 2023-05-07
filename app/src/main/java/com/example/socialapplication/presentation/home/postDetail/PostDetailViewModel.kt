package com.example.socialapplication.presentation.home.postDetail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapplication.data.remote.dto.CommentDto
import com.example.socialapplication.data.remote.service.CommentService
import com.example.socialapplication.domain.model.Comment
import com.example.socialapplication.domain.model.User
import com.example.socialapplication.presentation.home.home.PostState
import com.example.socialapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val commentService: CommentService
): ViewModel() {

    // 评论区头像数据
    private val _avatarState = mutableStateMapOf<Int, ByteArray>()
    val avatarState : SnapshotStateMap<Int, ByteArray> = _avatarState

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    // 顶部作者头像
    private val _authorAvatar = mutableStateOf(byteArrayOf(0))
    val authorAvatar : State<ByteArray> = _authorAvatar

    // 评论区列表数据
    private val _state = mutableStateOf(CommentState())
    val state: State<CommentState> = _state

    // 输入框内容
    private val _commentText = mutableStateOf("")
    val commentText =  _commentText

    fun onCommentTextChange(content: String) {
        _commentText.value = content
    }

    // 下载评论区头像
    fun downLoadImageByComment(index: Int, author: String) {
        viewModelScope.launch {
            _avatarState[index] = commentService.downLoadImageByAuthor(author)
        }
    }

    // 帖子作者头像
    fun downLoadImageByAuthor(author: String) {
        viewModelScope.launch {
            _authorAvatar.value = commentService.downLoadImageByAuthor(author)
        }
    }

    // 发送评论
    fun sendCommentPost(postId: String, author: String, content: String) {
        if (_commentText.value != "") {
            viewModelScope.launch {
                _state.value = state.value.copy(isLoading = true)
                val response = commentService.insertComment(postId, author, content)

                _state.value = state.value.copy(
                    comments = state.value.comments.toMutableList().apply {
                        add(response)
                    }
                )
            }
        }
        _commentText.value = ""
    }

    // 获得评论区内容
    fun getCommentByPostId(postId: String) {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            _avatarState.clear()
            val result = commentService.getCommentByPostId(postId)
            _state.value = state.value.copy(
                comments = result,
                isLoading = false
            )
        }
    }

    // 更新评论的点赞数
    fun updatePostZan(id: String, isIncrease: Boolean) {
        viewModelScope.launch {
            commentService.updateCommentZan(id, isIncrease)
        }
    }

}