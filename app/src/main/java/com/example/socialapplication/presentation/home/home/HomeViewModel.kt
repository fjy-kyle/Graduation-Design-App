package com.example.socialapplication.presentation.home.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Post
import com.example.socialapplication.data.remote.service.PostService
import com.example.socialapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postService: PostService
) : ViewModel() {

    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText

    // 首页的帖子列表数据
    private val _state = mutableStateOf(PostState())
    val state: State<PostState> = _state

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    // 使用mutableStateMapOf，来存储每一个列表项的作者头像
    private val _authorAvatar = mutableStateMapOf<Int, ByteArray>()
    val authorAvatar : SnapshotStateMap<Int, ByteArray> = _authorAvatar

    fun onSearchTextChange(text: String){
        _searchText.value = text
    }

    fun getAllPosts(){
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            val result = postService.getAllPost().reversed() //反转，使得最新发出的贴显示在前
            _state.value = state.value.copy(
                posts = result,
                isLoading = false
            )
        }
    }

    // 根据title查找帖子
    fun findPostsByTitle(title:String) {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            _authorAvatar.clear()
            val result = postService.findPostByTitle(title)
            _state.value = state.value.copy(
                posts = result,
                isLoading = false
            )

        }
    }

    fun updatePostZan(id: String, isIncrease: Boolean) {
        viewModelScope.launch {
            postService.updatePostZan(id,isIncrease)
        }
    }
    fun downLoadImageByAuthor(index: Int, author: String){
        viewModelScope.launch {
            _authorAvatar[index] = postService.downLoadImageByAuthor(author)
        }
    }
}


