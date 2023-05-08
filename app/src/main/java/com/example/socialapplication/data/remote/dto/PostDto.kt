package com.example.socialapplication.data.remote.dto

import com.example.data.model.Post
import com.example.socialapplication.domain.model.Message
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class PostDto (
    val title: String  = "",
    val content :String = "",
    val author: String = "",
    val timestamp: Long = 0,
    val zan: Int = 0,
    val commentCount: Int = 0,
    val authorAvatar :String = "",
    val authorNickname: String = "",
    val id : String
) {
    fun toPost(): Post {
        val data = Date(timestamp)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data)
        return Post(
            id = id,
            title = title,
            content = content,
            author = author,
            formattedTime = formattedDate,
            zan = zan,
            commentCount = commentCount,
            authorAvatar = authorAvatar,
            authorNickname = authorNickname
        )
    }
}