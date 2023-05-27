package com.example.socialapplication.presentation.welcome

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapplication.R
import com.example.socialapplication.data.remote.service.UserService
import com.example.socialapplication.domain.model.User
import com.example.socialapplication.main.SocialApp.Companion.context
import com.example.socialapplication.main.SocialApp.Companion.sharedPreferences
import com.example.socialapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject


/**
 * 用户登录与注册页面功能逻辑实现
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userService: UserService
): ViewModel() {

    // 验证是否符合登录要求
    val isLoginFormValid by derivedStateOf {
        loginUsernameText.value.isNotBlank() && loginPasswordText.value.length >=8
    }

    // 用户名正则(长度2~10，由中英文或数字组成)
    private val regex =  "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2,10}\$".toRegex()

    // 验证是否符合登录要求
    private val isRegisterFormValid by derivedStateOf {
        regex.matches(registerUsernameText.value) &&
                registerNicknameText.value.isNotBlank() &&
                registerPasswordText.value.length >= 8 &&
                (registerPasswordConfirmText.value == registerPasswordText.value)

    }

    // 注册时，选择头像
    private val _registerAvatarByteArray: MutableState<ByteArray> = mutableStateOf(drawableToByteArray(R.drawable.default_avatar))
    val registerAvatarByteArray  = _registerAvatarByteArray


    // 注册时，填写的用户名
    private val _registerUsernameText = mutableStateOf("")
    val registerUsernameText: State<String> = _registerUsernameText

    // 注册时，填写的昵称
    private val _registerNicknameText = mutableStateOf("")
    val registerNicknameText: State<String> = _registerNicknameText

    // 注册时，填写的密码
    private val _registerPasswordText = mutableStateOf("")
    val registerPasswordText: State<String> = _registerPasswordText

    // 注册时，填写的确认密码
    private val _registerPasswordConfirmText = mutableStateOf("")
    val registerPasswordConfirmText: State<String> = _registerPasswordConfirmText

    // 登录时，填写的用户名
    private val _loginUsernameText = mutableStateOf("")
    val loginUsernameText: State<String> = _loginUsernameText

    // 登录时，填写的密码
    private val _loginPasswordText = mutableStateOf("")
    val loginPasswordText: State<String> = _loginPasswordText

    private val _onJoinHome = MutableSharedFlow<String>()
    val onJoinHome = _onJoinHome.asSharedFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun onRegisterAvatarChange(byteArray: ByteArray){
        _registerAvatarByteArray.value = byteArray
    }
    fun onLoginUsernameChange(username: String) {
        _loginUsernameText.value = username
    }

    fun onLoginPasswordChange(password: String){
        _loginPasswordText.value = password
    }

    fun onRegisterUsernameChange(username: String) {
        _registerUsernameText.value = username
    }

    fun onRegisterNicknameChange(nickname: String){
        _registerNicknameText.value = nickname
    }

    fun onRegisterPasswordChange(password: String) {
        _registerPasswordText.value = password
    }

    fun onRegisterPasswordConfirmChange(rePassword: String){
        _registerPasswordConfirmText.value = rePassword
    }


    fun onLoginClick(){
        viewModelScope.launch {
            when(val response = userService.userLogin(_loginUsernameText.value, _loginPasswordText.value)) {
                is Resource.Success -> {
                    val user = response.data as User
                    val json = Json.encodeToString(user)
                    val a = withContext(Dispatchers.IO) {
                        URLEncoder.encode(json, StandardCharsets.UTF_8.toString())
                    }
                    saveUserInfo() // 登录成功，则在本地保存一份用户信息
                    _onJoinHome.emit(a)
                }
                is Resource.Error -> {
                    _toastEvent.emit(response.message!!)
                }
            }
        }
    }


    fun onRegisterClick(){
            viewModelScope.launch {
                if (isRegisterFormValid) {
                    when(val response = userService.userRegister(
                        _registerUsernameText.value, _registerPasswordText.value,
                        _registerNicknameText.value,_registerAvatarByteArray.value)) {
                        is Resource.Success -> {
                            val user = response.data as User
                            _loginUsernameText.value = user.username
                            _loginPasswordText.value = user.password
                            _toastEvent.emit("注册成功")
                        }
                        is Resource.Error -> {
                            _toastEvent.emit(response.message!!)
                        }
                    }
                } else outputErrorInfo()
            }
        }

    // 检查当前设备是否有用户登陆过
    fun checkLastUser() {
        if (sharedPreferences.getString("username","") != null) {
            _loginUsernameText.value = sharedPreferences.getString("username","")!!
            _loginPasswordText.value = sharedPreferences.getString("password","")!!
        }
    }

    // 保存当前用户用户名与密码
    private fun saveUserInfo(){
        sharedPreferences.edit().apply{
            putString("username", _loginUsernameText.value)
            putString("password", _loginPasswordText.value)
            apply()
        }
    }

    // 输出用户错误信息
    private suspend fun outputErrorInfo(){
        if (!regex.matches(_registerUsernameText.value) || _registerUsernameText.value.isBlank()) {
            _toastEvent.emit("用户名不符合要求")
        } else if (_registerNicknameText.value.isBlank()) {
            _toastEvent.emit("昵称不能为空")
        } else if (_registerPasswordText.value.isBlank()) {
            _toastEvent.emit("密码不能为空")
        } else if (_registerPasswordText.value.length < 8) {
            _toastEvent.emit("密码长度过短")
        } else if (_registerPasswordConfirmText.value != _registerPasswordText.value) {
            _toastEvent.emit("密码输入不一致")
        }
    }

    // drawable 转 ByteArray
    private fun drawableToByteArray(drawable: Int): ByteArray{
        val bitmap = BitmapFactory.decodeResource(context.resources,drawable)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100 ,stream)
        return stream.toByteArray()
    }
}