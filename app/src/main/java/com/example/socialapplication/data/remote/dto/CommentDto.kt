package com.example.socialapplication.data.remote.dto

import com.example.socialapplication.domain.model.Comment
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

// 评论数据模型
@Serializable
data class CommentDto(

    val id : String = "",
    val content: String = "", // 评论内容
    val author: String = "", // 评论作者
    val timestamp: Long = 0, // 评论时间
    val postId :String = "", // 属于哪个帖子
    val zan : Int = 0 ,// 获赞数
    val authorAvatar :String = "", // 评论作者头像
) {
    fun toComment() : Comment {
        val data = Date(timestamp)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data)
        return Comment(
            id = id,
            content = content,
            author = author,
            formattedTime = formattedDate,
            postId = postId,
            zan = zan,
            authorAvatar = authorAvatar
        )
    }
}
