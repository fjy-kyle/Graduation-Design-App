package com.example.socialapplication.domain.model

@kotlinx.serialization.Serializable
data class Comment(
    val id : String = "",
    val content: String = "", // 评论内容
    val author: String = "", // 评论作者
    val formattedTime: String = "", // 评论时间
    val postId :String = "", // 属于哪个帖子
    val zan : Int = 0 ,// 获赞数
    val authorAvatar : String = "", // 评论作者头像
    val authorNickname: String = "",
)