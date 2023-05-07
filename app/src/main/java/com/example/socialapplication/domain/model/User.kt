package com.example.socialapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val username: String = "",
    val password: String = "",
    val nickname: String? = "",
    val avatar : String? = "",
    val zanCount: Int = 0, // 获赞总数
    val postCount: Int = 0, // 发帖数
    val sign: String = "", // 个性签名
    val commentCount: Int = 0, // 评论数
)
