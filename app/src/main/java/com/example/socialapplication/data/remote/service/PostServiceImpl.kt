package com.example.socialapplication.data.remote.service

import android.util.Log
import com.example.data.model.Post
import com.example.socialapplication.data.remote.dto.BaseModel
import com.example.socialapplication.data.remote.dto.PostDto
import com.example.socialapplication.util.Resource
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class PostServiceImpl(
    private val client: HttpClient
): PostService {


    override suspend fun downLoadImageByAuthor(author: String): ByteArray {

        val httpResponse: HttpResponse = client.get(author) {
            onDownload { bytesSentTotal, contentLength ->
                println("Received $bytesSentTotal bytes from $contentLength")
            }
        }
        return httpResponse.readBytes()
    }

    override suspend fun getAllPost(): List<Post> {
        return try {
            client.get<List<PostDto>>(PostService.EndPoint.GetAllPost.url).map {
                it.toPost()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun updatePostZan(id: String, isIncrease: Boolean) {
        val flag = if(isIncrease) "1" else "0"
        client.put<String>(PostService.EndPoint.UpdatePostZan.url + "?id=$id" + "&flag=$flag")
    }


    override suspend fun insertPost(
        title: String,
        content: String,
        author: String
    ) :  Resource<Any>{
        val response= client.post<BaseModel<PostDto>>(
            PostService.EndPoint.InsertPost.url +
                "?title=$title&content=$content&author=$author"
        ) {
            body = content
            headers{
                append(HttpHeaders.ContentType, "multipart/form-data")
            }
        }
        return if (response.errorCode ==0 && response.data != null) {
            Resource.Success(response.data.toPost())
        } else {
            Resource.Error(message = response.errorMsg!!)
        }
    }

    override suspend fun findPostById(id: String): Resource<Any> {
        val response = client.get<BaseModel<PostDto>>(
            "${PostService.EndPoint.FindPostById.url}?id=$id"
        )
        return if (response.errorCode ==0 && response.data != null) {
            Resource.Success(response.data.toPost())
        } else {
            Resource.Error(message = response.errorMsg!!)
        }
    }

    override suspend fun findPostByTitle(title: String) : List<Post>{
        return try {
            client.get<BaseModel<List<PostDto>>>("${PostService.EndPoint.FindPostByTitle.url}?title=$title").data!!.map {
                it.toPost()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}