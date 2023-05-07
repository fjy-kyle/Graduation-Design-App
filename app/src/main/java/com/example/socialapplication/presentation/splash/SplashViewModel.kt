package com.example.socialapplication.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapplication.data.remote.service.UserService
import com.example.socialapplication.domain.model.User
import com.example.socialapplication.main.SocialApp
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
class SplashViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel(){

    var username = ""
    var password = ""

    private val _onJoinHome = MutableSharedFlow<String>()
    val onJoinHome = _onJoinHome.asSharedFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    // 检查用户之前是否已经登录
    fun isUserLogin(): Boolean {
        username = SocialApp.sharedPreferences.getString("username","")!!
        password = SocialApp.sharedPreferences.getString("password","")!!
        return username.isNotBlank() && password.isNotBlank()
    }

    fun goLogin(){
        viewModelScope.launch {
            when(val response = userService.userLogin(username, password)) {
                is Resource.Success -> {
                    val user = response.data as User
                    val json = Json.encodeToString(user)
                    val a = withContext(Dispatchers.IO) {
                        URLEncoder.encode(json, StandardCharsets.UTF_8.toString())
                    }
                    _onJoinHome.emit(a)

                }
                is Resource.Error -> {
                    _toastEvent.emit(response.message!!)
                }
            }
        }
    }
}

