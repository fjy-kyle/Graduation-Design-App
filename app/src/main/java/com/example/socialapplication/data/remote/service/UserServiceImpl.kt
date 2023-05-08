package com.example.socialapplication.data.remote.service


import android.util.Log
import com.example.socialapplication.data.remote.dto.BaseModel
import com.example.socialapplication.data.remote.dto.UserDto
import com.example.socialapplication.domain.model.User
import com.example.socialapplication.util.Resource
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.*
import java.io.File


class UserServiceImpl(
    private val client: HttpClient
) : UserService {

    override suspend fun userLogin(username: String, password: String): Resource<Any> {
        return try {
            val response = client.get<BaseModel<UserDto>>(
                UserService.EndPoint.UserLogin.url +
                        "?username=$username&password=$password"
            )
            if (response.errorCode == 0 && response.data != null) {
                Resource.Success(response.data.toUser())
            } else {
                Resource.Error(message = response.errorMsg!!)
            }
        } catch (e:Exception) {
            e.printStackTrace()
            Resource.Error(message = e.localizedMessage ?: "出现未知错误")
        }
    }


    override suspend fun userRegister(
        username: String,
        password: String,
        nickname: String,
        avatarByteArray: ByteArray
    ): Resource<Any> {

        return try {
            val response = client.post<BaseModel<UserDto>>(UserService.EndPoint.UserRegister.url +
                    "?username=$username" +
                    "&nickname=$nickname" +
                    "&password=$password")
            {
                body = avatarByteArray
                headers  {
                    append(HttpHeaders.ContentType, "image/png")
                }
            }
            if (response.errorCode == 0 && response.data != null) {
                Resource.Success(response.data.toUser())
            } else {
                Resource.Error(message = response.errorMsg!!)
            }
        } catch (e:Exception) {
            e.printStackTrace()
            Resource.Error(message = e.localizedMessage ?: "出现未知错误")
        }
    }

    override suspend fun getUserByUserName(username: String): User {
        return client.get<UserDto>(UserService.EndPoint.GetUserByName.url + "?username=$username").toUser()
    }

    override suspend fun updateSignByName(name: String, sign: String) {
        client.post<String>(UserService.EndPoint.UpdateSignByName.url + "?username=$name&sign=$sign")
    }

    override suspend fun downLoadImageByAuthor(author: String): ByteArray {

        val httpResponse: HttpResponse = client.get(author) {
            onDownload { bytesSentTotal, contentLength ->
                println("Received $bytesSentTotal bytes from $contentLength")
            }
        }

        return httpResponse.readBytes()
    }

    override suspend fun updateUserAvatar(username: String, avatar: ByteArray) {
        // 更换头像
        client.put<String>("${UserService.EndPoint.UpdateUserAvatar.url}?username=$username"){
            body = avatar
            headers  {
                append(HttpHeaders.ContentType, "image/png")
            }
        }
        Log.d("updateUserAvatar","123456")
    }

}

