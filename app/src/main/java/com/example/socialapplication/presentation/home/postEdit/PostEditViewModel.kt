package com.example.socialapplication.presentation.home.postEdit

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapplication.data.remote.service.PostService
import com.example.socialapplication.main.SocialApp
import com.example.socialapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostEditViewModel @Inject constructor(
    private val postService: PostService
) : ViewModel(){


    private val _titleText = mutableStateOf("")
    val titleText = _titleText

    private val _contentText = mutableStateOf("")
    val contentText = _contentText

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    private val _onJoinHome = MutableSharedFlow<String>()
    val onJoinHome = _onJoinHome.asSharedFlow()

    fun onTitleTextChange(title: String){
        _titleText.value = title
    }

    fun onContentTextChange(content: String){
        _contentText.value = content
    }

    // 验证帖子是否符合要求
    private val isTitleFormValid by derivedStateOf {
        _titleText.value.length in 5..30
    }


    fun sendPostClick(){
        val author = SocialApp.sharedPreferences.getString("username","")!!

        viewModelScope.launch {
            if (isTitleFormValid) {
                when (val response = postService.insertPost(_titleText.value, _contentText.value, author)) {
                    is Resource.Success -> {
                        _toastEvent.emit("发帖成功")
                        _onJoinHome.emit("")
                    }
                    is Resource.Error -> {
                        _toastEvent.emit(response.message!!)
                    }
                }
            } else {
                _toastEvent.emit("标题不符合要求")
            }
        }
    }
}