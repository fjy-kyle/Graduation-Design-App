package com.example.socialapplication.data.remote.service

import android.util.Log
import com.example.socialapplication.data.remote.dto.BaseModel
import com.example.socialapplication.data.remote.dto.CommentDto
import com.example.socialapplication.util.Resource
import io.ktor.client.*
import io.ktor.client.request.*
import com.example.socialapplication.domain.model.Comment
import io.ktor.client.features.*
import io.ktor.client.statement.*

class CommentServiceImpl(
    private val client: HttpClient
) : CommentService{

    override suspend fun getCommentByPostId(postId: String): List<Comment> {
        val response = client.get<List<CommentDto>>(
            "${CommentService.EndPoint.GetCommentByPostId.url}?id=$postId"
        ).map { it.toComment() }
        return if (response == emptyList<Comment>()) {
            emptyList()
        } else {
            response
        }
    }

    override suspend fun insertComment(postId: String, author: String, content: String): Comment {
        val response = client.post<CommentDto>(
            "${CommentService.EndPoint.InsertComment.url}?id=$postId&author=$author&content=$content"
        )
        return response.toComment()
    }

    override suspend fun updateCommentZan(commentId: String, isIncrease: Boolean) {
        val flag = if(isIncrease) "1" else "0"
        client.put<String>("${CommentService.EndPoint.UpdateCommentZan.url}?id=$commentId&flag=$flag")
    }

    override suspend fun downLoadImageByAuthor(author: String): ByteArray {
        Log.d("avatar",author)
        val httpResponse: HttpResponse = client.get(author) {
            onDownload { bytesSentTotal, contentLength ->
                println("Received $bytesSentTotal bytes from $contentLength")
            }
        }
        return httpResponse.readBytes()
    }
}