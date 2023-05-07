package com.example.socialapplication.data.remote.dto

import com.example.socialapplication.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
class UserDto(
    val id: String = "",
    val username: String = "",
    val password: String = "",
    val nickname: String? = "",
    val avatar: String? = "",
    val zanCount: Int = 0, // 获赞总数
    val postCount: Int = 0, // 发帖数
    val sign: String = "", // 个性签名
    val commentCount: Int = 0, // 评论数
) {
    fun toUser() = User(id, username, password, nickname, avatar,zanCount,postCount, sign, commentCount)

}