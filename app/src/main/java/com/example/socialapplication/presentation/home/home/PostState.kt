package com.example.socialapplication.presentation.home.home

import com.example.data.model.Post

data class PostState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false
)