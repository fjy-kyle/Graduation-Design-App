package com.example.chat_app.presentation.chat

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.socialapplication.domain.model.Message
import com.example.socialapplication.main.SocialApp
import com.example.socialapplication.presentation.home.chat.ChatViewModel
import com.example.socialapplication.ui.theme.OldRose
import com.example.socialapplication.ui.theme.SocialApplicationTheme
import com.example.socialapplication.ui.theme.Vanilla
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val username = SocialApp.sharedPreferences.getString("username","")

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        Log.d("onStart","you were in DisposableEffect")
        val observer = LifecycleEventObserver { _, event ->

            if (event == Lifecycle.Event.ON_START) {
                viewModel.connectToChat()
            } else if(event == Lifecycle.Event.ON_STOP) {
                viewModel.disconnect()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            Log.d("onStart","you were out DisposableEffect")
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp, top = 0.dp, end = 6.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            reverseLayout = true
        ){
            items(state.messages) { message ->
                val isOwnMessage = message.username == username
                Column(modifier = Modifier.fillMaxWidth()) {
                    MessageItem(
                        message = message,
                        isOwnMessage = isOwnMessage,
                        modifier = Modifier.align(
                            if (isOwnMessage) Alignment.End else Alignment.Start
                        )
                    )
                }
            }
        }
        InputBar(viewModel)
    }
}


@Composable
fun MessageItem(modifier: Modifier, isOwnMessage: Boolean, message: Message) {
    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (isOwnMessage) 15.dp else 0.dp,
                    topEnd = 15.dp,
                    bottomStart = 15.dp,
                    bottomEnd = if (isOwnMessage) 0.dp else 15.dp
                )
            )
            .background(
                if (isOwnMessage) OldRose else Vanilla
            )
            .padding(16.dp)
    ) {
        Text(
            text = message.username,
            color = Color.Black,
            fontSize = 10.sp
        )
        Text(
            text = message.text,
            color = Color.Black,
            modifier = Modifier.widthIn(max = 250.dp)
        )
        Text(
            text = message.formattedTime,
            color = Color.Black,
            fontSize = 10.sp
        )
    }
}



@Composable
fun InputBar(viewModel: ChatViewModel){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(247, 247, 247)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = viewModel.messageText.value,
            onValueChange = viewModel::onMessageChange,
            placeholder = {
                Text(text = "发条消息吧")
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
        IconButton(onClick = viewModel::sendMessage) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "send"
            )
        }
    }
}

@Preview
@Composable
fun messageItem(){
    SocialApplicationTheme() {
        MessageItem(isOwnMessage = true, message = Message(text = "hello"), modifier = Modifier)

    }
}