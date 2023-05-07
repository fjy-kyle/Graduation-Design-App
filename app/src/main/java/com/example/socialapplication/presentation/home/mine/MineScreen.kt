package com.example.socialapplication.presentation.home.mine


import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.socialapplication.domain.model.User
import com.example.socialapplication.main.SocialApp.Companion.context

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MineScreen(
    viewModel: MineViewModel = hiltViewModel(),
    onNavigate: (String)->Unit,
    user: User,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        Log.d("onStart","you were in DisposableEffect")
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.userInfoInit(user = user)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            Log.d("onStart","you were out DisposableEffect")
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar( onNavigate )
        }
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                RoundImage(
                    image = viewModel.userAvatar.value,
                    modifier = Modifier
                        .size(100.dp)
                        .weight(3f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                StatSection(viewModel = viewModel, modifier = Modifier.weight(7f))
            }
            ProfileDescription(
                displayName = viewModel.username.value,
                signText = viewModel.signText.value,
                viewModel::onSignTextChange,
                viewModel::updateUserSignText
            )
        }
    }
}

@Composable
fun TopAppBar(
    onNavigate: (String)->Unit,
){
    TopAppBar(
        title = {
            //标题
            Text(text = "我的",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center
            )
        },
        // 注销
        actions = {
            IconButton(onClick =  { onNavigate("login_screen") } ) {
                Icon(Icons.Default.ExitToApp, null)
            }
        }
    )
}
@Composable
fun RoundImage(
    image: ByteArray,
    modifier: Modifier  = Modifier
){
    AsyncImage(
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .border(width = 1.dp,
                color = Color.LightGray,
                shape = CircleShape
            )
            .padding(3.dp)
            .clip(CircleShape),
        model = ImageRequest.Builder(LocalContext.current)
            .data(image)
            .crossfade(true)
            .build(),
        contentDescription = "Avatar Image",
        contentScale = ContentScale.Crop
    )
}

@Composable
fun StatSection(viewModel: MineViewModel, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ) {
        ProfileStat(numberText = viewModel.postCount.value, text = "发帖")
        ProfileStat(numberText = viewModel.zanCount.value, text = "获赞")
        ProfileStat(numberText = viewModel.commentCount.value, text = "评论")
    }
}

@Composable
fun ProfileStat(
    numberText: String,
    text: String,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
         Text(
             text = numberText,
             fontWeight = FontWeight.Bold,
             fontSize = 20.sp
         )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text)
    }
}

@Composable
fun ProfileDescription(
    displayName: String,
    signText: String,
    onSignTextChange: (String) -> Unit,
    updateSign: () ->Unit
){
    val letterSpacing = 0.5.sp
    val lineHeight = 20.sp
    var enabled by remember {
        mutableStateOf(false)
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
    ) {
        Text(
            text = displayName,
            fontWeight = FontWeight.Bold,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
        OutlinedTextField(
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                letterSpacing = letterSpacing,
                lineHeight = lineHeight
            ),
            value = signText,
            onValueChange = onSignTextChange,
            enabled = enabled,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                backgroundColor = Color.White,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent
            ),
            placeholder = {
                Text(
                    text = if (signText=="") "这个人很神秘，什么都没有写" else signText,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = letterSpacing,
                    lineHeight = lineHeight
                )
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            enabled = !enabled
                            updateSign() },
                    imageVector = Icons.Default.Edit,
                    contentDescription = ""
                )
            }
        )
    }
}