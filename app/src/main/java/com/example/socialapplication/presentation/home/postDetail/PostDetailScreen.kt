package com.example.socialapplication.presentation.home.postDetail


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Post
import com.example.socialapplication.domain.model.Comment
import com.example.socialapplication.main.SocialApp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel = hiltViewModel(),
    post: Post,
    navController: NavController
) {
    LaunchedEffect(key1 = true){
        viewModel.downLoadImageByAuthor(post.authorAvatar) // 下载文章作者头像
        viewModel.getCommentByPostId(post.id) // 获取评论区数据
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    //标题
                    Text(text = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    //返回按钮
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                }
            )
        },
    ) { PaddingValues ->
        val state = viewModel.state.value
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp, top = 0.dp, end = 6.dp)
                        .fillMaxWidth()
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = post.title, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth()) {
                                AsyncImage(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .size(50.dp)
                                        .clip(CircleShape),
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(viewModel.authorAvatar.value)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Avatar Image",
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = post.author,
                                        fontSize = 12.sp)
                                    Text(
                                        modifier = Modifier.padding(top=5.dp),
                                        text = post.formattedTime
                                    )
                                }
                            }
                            Text(text = post.content, style = MaterialTheme.typography.body2)
                        }
                        Divider()
                        Text(text = "评论", fontSize = 20.sp, fontStyle = FontStyle.Italic)
                        Divider()
                    }
                    itemsIndexed(state.comments) { index, comment ->
                        CommentItem(viewModel, index, comment)
                        Divider(modifier = Modifier.height(1.dp))
                    }
                }
                InputBar(post, viewModel)
            }
        }
    }



@Composable
fun CommentItem(viewModel: PostDetailViewModel, index: Int, comment:Comment){

    // 获取当前帖子作者的头像
    LaunchedEffect(key1 = true){
        viewModel.downLoadImageByComment(index, comment.authorAvatar)
    }

    // 获取当前index 对应的作者头像
    val image = viewModel.avatarState[index]

    var clickState by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            modifier = Modifier
                .align(Alignment.Top)
                .padding(5.dp)
                .size(30.dp)
                .clip(CircleShape),
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .crossfade(true)
                .build(),
            contentDescription = "Comment Image",
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier.padding(3.dp),
                text = comment.author,
                style = MaterialTheme.typography.subtitle2)
            Text(modifier = Modifier.padding(3.dp),
                text = comment.formattedTime,
                fontSize = 10.sp)
            Text(text = comment.content )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    modifier = Modifier.padding(end = 5.dp)
                        .size(20.dp)
                        .clickable {
                            clickState = !clickState
                            viewModel.updatePostZan(comment.id, clickState)
                        },
                    contentDescription = "点赞",
                    tint = if (clickState) {
                        MaterialTheme.colors.primary
                    } else {
                        Color.Gray
                    }
                )
                Text(
                    text  = if (clickState) {
                        (comment.zan+1).toString()
                    } else {
                        (comment.zan).toString()
                    })
            }
        }
    }
}

@Composable
fun InputBar(post: Post, viewModel: PostDetailViewModel){
    // 取得当前用户名
    val username = SocialApp.sharedPreferences.getString("username","")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(247, 247, 247)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = viewModel.commentText.value,
            onValueChange = viewModel::onCommentTextChange,
            placeholder = {
                Text(text = "发表一下看法吧")
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .weight(1f)
                .padding(10.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        IconButton(onClick = {
            viewModel.sendCommentPost(postId = post.id, username!!, viewModel.commentText.value)
        }) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "send"
            )
        }
    }
}
@Preview
@Composable
fun PostDetailScreenPreview() {
//    PostDetailScreen(Post())
}

