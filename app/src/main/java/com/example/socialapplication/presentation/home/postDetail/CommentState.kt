package com.example.socialapplication.presentation.home.postDetail

import com.example.data.model.Post
import com.example.socialapplication.domain.model.Comment


data class CommentState(
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false
)