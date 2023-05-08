package com.example.socialapplication.presentation.home.mine

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapplication.data.remote.service.UserService
import com.example.socialapplication.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel(){

    // 用户头像
    private val _userAvatar = mutableStateOf(byteArrayOf(0))
    val userAvatar : State<ByteArray> = _userAvatar

    // 点赞数
    private val _zanCount = mutableStateOf("")
    val zanCount : State<String> = _zanCount

    // 发帖数
    private val _postCount = mutableStateOf("")
    val postCount : State<String> = _postCount

    // 用户名
    private val _username = mutableStateOf("")
    val username : State<String> = _username

    // 用户名
    private val _commentCount = mutableStateOf("")
    val commentCount : State<String> = _commentCount

    // 个性签名
    private val _signText = mutableStateOf("")
    val signText : State<String> = _signText


    fun userInfoInit(user: User){
        getUserByName(user.username)
    }

    private fun getUserByName(username:String){
        viewModelScope.launch {
            val user = userService.getUserByUserName(username)
            _postCount.value = user.postCount.toString()
            _username.value = user.username
            _signText.value = user.sign
            _zanCount.value = user.zanCount.toString()
            _commentCount.value = user.commentCount.toString()
            Log.d("------------------",user.avatar!!)
            _userAvatar.value = userService.downLoadImageByAuthor(user.avatar)
        }
    }

    fun onSignTextChange(text: String){
        _signText.value = text
    }

    fun onAvatarChange(avatar: ByteArray) {
        _userAvatar.value = avatar
    }

    // 更新用户个性签名
    fun updateUserSignText() {
        if (signText.value.isNotEmpty()){
            viewModelScope.launch {
                userService.updateSignByName(_username.value, _signText.value)
            }
        }
    }

    fun updateUserAvatar(username: String, avatar: ByteArray) {
        viewModelScope.launch {
            userService.updateUserAvatar(username, avatar)
        }
    }

}