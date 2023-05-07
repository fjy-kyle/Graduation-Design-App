package com.example.socialapplication.presentation.home.home


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Post
import com.example.socialapplication.main.SocialApp
import com.example.socialapplication.main.SocialApp.Companion.context
import com.example.socialapplication.ui.theme.Purple500
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigate:(String) -> Unit,
    navController: NavController
) {

    LaunchedEffect(key1 = true) {
        viewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner){
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.getAllPosts()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val state = viewModel.state.value

    Scaffold(
        backgroundColor = MaterialTheme.colors.primarySurface,
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 2.dp, 24.dp, 6.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = viewModel.searchText.value,
                        singleLine = true,
                        onValueChange = viewModel::onSearchTextChange,
                        modifier = Modifier
                            .padding(start = 24.dp)
                            .weight(1f),
                        textStyle = TextStyle(fontSize = 15.sp)
                    ) {
                        if (viewModel.searchText.value.isEmpty()) {
                            Text(text = "搜搜看?", color = Color(0xffb4b4b4), fontSize = 15.sp)
                        }
                        it()
                    }
                    Box(Modifier
                        .padding(6.dp)
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(Purple500)
                    ) {
                        IconButton(onClick = {
                            viewModel.findPostsByTitle(viewModel.searchText.value)
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "search",
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center), tint = Color.White
                                )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                backgroundColor = MaterialTheme.colors.primary,
                onClick = {
                    // 跳转到帖子编辑界面
                    onNavigate("postEdit_screen")
            } ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "", tint = Color.White)
            }
        },
    ) { PaddingValues ->
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.value.isLoading)
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                viewModel.getAllPosts()
            },
            indicator = { state, refreshTrigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTrigger,
                    contentColor = MaterialTheme.colors.primary
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(PaddingValues)
                    .fillMaxWidth(),
            ){
                itemsIndexed(state.posts) { index, post ->
                    PostItem(viewModel, post, index, navController)
                }
            }
        }
    }
}

@Composable
fun PostItem(
    viewModel: HomeViewModel,
    post: Post,
    index: Int,
    navController: NavController
){
    // 获取当前帖子作者的头像
    LaunchedEffect(key1 = true){
        viewModel.downLoadImageByAuthor(index, post.authorAvatar)
    }

    // 获取当前index 对应的作者头像
    val image = viewModel.authorAvatar[index]

    var clickState by remember {
        mutableStateOf(false)
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                // 跳转到详情页面实现-------------------------------------------------------------------------------------------
                val json = Json.encodeToString(post)
                val  a= URLEncoder.encode(json, StandardCharsets.UTF_8.toString())
                navController.navigate("postDetail_screen/$a")
            }
    ) {
        Column()
        {
            Row{
                AsyncImage(
                    modifier = Modifier
                        .padding(5.dp)
                        .size(50.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar Image",
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = post.author,
                        fontFamily = FontFamily.Default
                    )
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = post.formattedTime,
                        fontSize = 10.sp
                    )
                }
            }
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = post.title, fontSize = 20.sp)
                Text(
                    maxLines = 5,
                    text = post.content,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(

            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Default.Pending,
                        contentDescription = "",
                        tint = Color.Gray)
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = post.commentCount.toString(),
                        color = Color.Gray
                    )
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(modifier = Modifier
                        .clickable {
                            clickState = !clickState
                            viewModel.updatePostZan(post.id, clickState)
                        },
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "",
                        tint = if (clickState) {
                            MaterialTheme.colors.primary
                        } else {
                            Color.Gray
                        }
                    )
                    Text(
                        modifier = Modifier.padding(5.dp),
                        color = Color.Gray,
                        text = if (clickState) {
                            (post.zan+1).toString()
                        } else {
                            (post.zan).toString()
                        }
                    )

                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {

}

