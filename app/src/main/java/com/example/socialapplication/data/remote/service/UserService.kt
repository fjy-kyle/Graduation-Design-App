package com.example.socialapplication.data.remote.service

import android.net.Uri
import com.example.socialapplication.domain.model.User
import com.example.socialapplication.util.Resource


interface UserService {

    suspend fun userLogin(
        username: String,
        password: String
    ) : Resource<Any>

    suspend fun userRegister(
        username: String,
        password: String,
        nickname: String,
        avatarByteArray: ByteArray
    ) : Resource<Any>


    suspend fun getUserByUserName(username: String): User

    suspend fun downLoadImageByAuthor(author: String): ByteArray

    suspend fun updateSignByName(name: String, sign: String)

    companion object {
        private const val HOST = "47.108.253.91"
        private const val PORT = "8008"
        const val BASE_URL = "http://$HOST:$PORT"
    }

    sealed class EndPoint(val url: String) {
        object UserLogin: EndPoint("$BASE_URL/user/login")
        object UserRegister: EndPoint("$BASE_URL/user/register")
        object GetUserByName: EndPoint("$BASE_URL/user/findByName")
        object UpdateSignByName: EndPoint("$BASE_URL/user/updateSign")
        object UserUpload: EndPoint("$BASE_URL/upload") // 更换头像，待用
    }
}