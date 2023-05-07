package com.example.socialapplication.data.remote.dto


@kotlinx.serialization.Serializable
data class BaseModel<T> (
    val data: T? = null,
    val errorMsg: String? = null,
    val errorCode :Int = 0
)