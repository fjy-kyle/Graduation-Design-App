package com.example.data.model


import kotlinx.serialization.Serializable


@Serializable
data class Post (
    val id : String = "",
    val title: String  = "",
    val content :String = "",
    val author: String = "",
    val formattedTime: String = "",
    val zan: Int = 0,
    val commentCount: Int = 0,
    val authorAvatar :String = "",
    val authorNickname: String = "",
)