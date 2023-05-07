package com.example.socialapplication.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomItemScreen(val route: String, val title:String, val icon: ImageVector) {
    object Home: BottomItemScreen("home_screen","首页",Icons.Default.Home)
    object ChatRoom: BottomItemScreen("chat_screen","聊天室",Icons.Default.Message)
    object Mine: BottomItemScreen("mine_screen","我的",Icons.Default.Person)
}
