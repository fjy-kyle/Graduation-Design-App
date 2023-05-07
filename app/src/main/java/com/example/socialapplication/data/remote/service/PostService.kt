package com.example.socialapplication.data.remote.service

import androidx.compose.ui.platform.InspectableModifier
import com.example.data.model.Post
import com.example.socialapplication.util.Resource

interface PostService {


    suspend fun getAllPost(): List<Post>

    suspend fun insertPost(
        title: String,
        content: String,
        author: String
    ):  Resource<Any>

    suspend fun findPostById(id: String): Resource<Any>

    suspend fun findPostByTitle(title: String): List<Post>

    suspend fun downLoadImageByAuthor(author: String): ByteArray

    suspend fun updatePostZan(id: String, isIncrease:Boolean)

    companion object {
        private const val HOST = "47.108.253.91"
        private const val PORT = "8008"
        const val BASE_URL = "http://$HOST:$PORT"
    }

    sealed class EndPoint(val url: String) {
        object GetAllPost: EndPoint("${BASE_URL}/posts")
        object InsertPost: EndPoint("${BASE_URL}/posts/insert")
        object FindPostById: EndPoint("${BASE_URL}/posts/findById")
        object FindPostByTitle: EndPoint("${BASE_URL}/posts/findByTitle")
        object UpdatePostZan: EndPoint("${BASE_URL}/posts/updateZan")
    }
}