package com.example.socialapplication.data.remote.service

import com.example.socialapplication.util.Resource
import com.example.socialapplication.domain.model.Comment
import com.example.socialapplication.domain.model.User

interface CommentService {

    suspend fun getCommentByPostId(postId: String): List<Comment>

    suspend fun insertComment(postId: String, author: String, content: String): Comment

    suspend fun updateCommentZan(commentId: String, isIncrease:Boolean)

    suspend fun downLoadImageByAuthor(author: String): ByteArray


    companion object {
        private const val HOST = "47.108.253.91"
        private const val PORT = "8008"
        const val BASE_URL = "http://$HOST:$PORT"
    }

    sealed class EndPoint(val url: String) {
        object GetCommentByPostId: EndPoint("${BASE_URL}/comments")
        object InsertComment: EndPoint("${BASE_URL}/comments/insert")
        object UpdateCommentZan: EndPoint("${BASE_URL}/comments/updateZan")
    }
}