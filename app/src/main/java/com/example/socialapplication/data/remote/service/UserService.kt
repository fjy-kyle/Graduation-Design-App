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

    suspend fun updateUserAvatar(username: String, avatar: ByteArray)

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
        object UpdateUserAvatar: EndPoint("$BASE_URL/user/updateAvatar") // 更换头像，待用
    }
}