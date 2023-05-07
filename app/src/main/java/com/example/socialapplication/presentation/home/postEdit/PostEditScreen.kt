package com.example.socialapplication.presentation.home.home


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.socialapplication.main.SocialApp.Companion.context
import com.example.socialapplication.presentation.home.postEdit.PostEditViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PostEditScreen(
    viewModel: PostEditViewModel = hiltViewModel(),
    onNavigate: () -> Unit
) {
    LaunchedEffect(key1 = true){
        viewModel.onJoinHome.collectLatest {
            onNavigate()
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    //标题
                    Text(text = "写个贴子吧",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                },
                actions = {
                    IconButton(onClick = viewModel::sendPostClick) {
                        Icon(Icons.Default.Send, null)
                    }
                },
                navigationIcon = {
                    //返回按钮
                    IconButton(onClick = onNavigate ) {
                        Icon(imageVector = Icons.Default.Undo, null, tint = Color.White)
                    }
                }
            )
        }
    ) { PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues)
        ) {
            TextField(
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth(),
                value = viewModel.titleText.value,
                placeholder = {
                    Text(text = "请输入完整帖子标题（5-31个字）", fontWeight = FontWeight.Bold)
                },
                onValueChange = viewModel::onTitleTextChange,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    backgroundColor = Color.White
                )

            )
            Divider()
            TextField(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                value = viewModel.contentText.value,
                placeholder = {
                    Text(text = "来吧，尽情发挥吧")
                },
                onValueChange = viewModel::onContentTextChange ,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    backgroundColor = Color.White
                )
            )
        }
    }

}

@Preview
@Composable
fun PostEditScreenPreview() {

}

