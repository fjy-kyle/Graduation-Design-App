package com.example.socialapplication.presentation.welcome


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.socialapplication.R
import com.example.socialapplication.main.SocialApp
import com.example.socialapplication.ui.theme.Purple200
import com.example.socialapplication.ui.theme.Purple500
import kotlinx.coroutines.flow.collectLatest
import java.io.ByteArrayOutputStream


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigate: (String)-> Unit
) {

    // 用于控制登录密码的显示/隐藏
    var isPasswordVisible1 by remember {
        mutableStateOf(false)
    }

    // 用于控制注册密码的显示/隐藏
    var isPasswordVisible2 by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    // 打开系统相册
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ){
        if (it != null) {
            Log.d("PhotoPicker","Selected URI: $it")
            // 根据Uri获得bitmap
            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(it))
            // 进行压缩
            val newBitmap = SocialApp.compressBitmap(bitmap,200.0,200.0)

            val stream = ByteArrayOutputStream()
            newBitmap!!.compress(Bitmap.CompressFormat.PNG, 100 ,stream)
            //转成byteArray类型，上传至服务器
            viewModel.onRegisterAvatarChange(stream.toByteArray())

            Log.d("PhotoPicker","Selected Bitmap: ${bitmap.height}")
        } else {
            Log.d("PhotoPicker","No media selected")
        }
    }



    LaunchedEffect(key1 = true) {
        viewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.onJoinHome.collectLatest { json->
            onNavigate("main_screen/$json")
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner){
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.checkLastUser()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    Scaffold(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.background(
            brush = Brush.verticalGradient(
                listOf(Purple500,Purple200)
            )
        )
    ) { PaddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(PaddingValues),
            verticalArrangement = Arrangement.Top
        ){
            Image(
                painter = painterResource(id = R.drawable.welcome_logo),
                contentDescription = "",
                modifier = Modifier
                    .weight(1f)
                    .size(350.dp),
                colorFilter = ColorFilter.tint(Color.White)

            )
            var isVisible by remember {
                mutableStateOf(true)
            }
            AnimatedContent(
                targetState = isVisible,
                modifier = Modifier.weight(2f),
                transitionSpec = {
                    slideInHorizontally(
                        animationSpec = tween(durationMillis = 1000)
                    ) {
                        if (isVisible) -it else it
                    } with slideOutHorizontally(
                        animationSpec = tween(durationMillis = 1000)
                    ) {
                        if (isVisible) it else -it
                    }
                }
            ) {
                if (it) {
                    // 登录卡
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                        ) {
                            Text(color = Color.Black, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, text = "欢迎回来!")
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Spacer(modifier = Modifier.weight(0.5f))
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = viewModel.loginUsernameText.value,
                                    onValueChange = viewModel::onLoginUsernameChange,
                                    label = { Text(text = "用户名")},
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.None),
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    trailingIcon = {
                                        if (viewModel.loginUsernameText.value.isNotBlank()) {
                                            IconButton(onClick = { viewModel.onLoginUsernameChange("") }) {
                                                Icon(imageVector = Icons.Filled.Clear, contentDescription = "")
                                            }
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Person, contentDescription = "", tint = MaterialTheme.colors.primary)
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = viewModel.loginPasswordText.value,
                                    onValueChange = viewModel::onLoginPasswordChange,
                                    singleLine = true,
                                    label = { Text(text = "密码")},
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.None),
                                    trailingIcon = {
                                        IconButton(onClick = { isPasswordVisible1 = !isPasswordVisible1 }) {
                                            Icon(imageVector = if (isPasswordVisible1)Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Password Toggle")
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    visualTransformation = if (isPasswordVisible1) VisualTransformation.None else PasswordVisualTransformation(),
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Lock, contentDescription = "", tint = MaterialTheme.colors.primary)
                                    }
                                )
                                Spacer(modifier = Modifier.height(26.dp))
                                Button(
                                    onClick = viewModel::onLoginClick,
                                    enabled = viewModel.isLoginFormValid,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row {
                                        Text(text = "登录",modifier = Modifier.padding(top=2.dp))
                                        Icon(imageVector = Icons.Default.Login, contentDescription = "")
                                    }
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = {
                                    isVisible = !isVisible
                                    //onNavigate("register_screen")
                                }) {
                                    Row {
                                        Text(text = "创建一个新用户?",modifier = Modifier.padding(top=2.dp))
                                        Icon(imageVector = Icons.Default.Input, contentDescription = "")
                                    }

                                }
                            }
                        }
                    }
                } else {
                    // 注册卡
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                                AsyncImage(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            photoPicker.launch(
                                                PickVisualMediaRequest(
                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                )
                                            )
                                        },
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(
                                            if (viewModel.registerAvatarByteArray.value.contentEquals(byteArrayOf(0))){
                                                R.drawable.default_avatar // 默认头像
                                            }else {
                                                viewModel.registerAvatarByteArray.value // 选择后的头像
                                            }
                                        )
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Avatar Image",
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    value = viewModel.registerUsernameText.value,
                                    onValueChange = viewModel::onRegisterUsernameChange,
                                    placeholder = {
                                        Text(text = "长度2~10，由中英文或数字组成", fontSize = 12.sp)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp),
                                    trailingIcon = {
                                        if (viewModel.registerUsernameText.value.isNotBlank()) {
                                            IconButton(onClick = { viewModel.onRegisterUsernameChange("")  }) {
                                                Icon(imageVector = Icons.Default.Clear, contentDescription = "")
                                            }
                                        }
                                    },
                                    label = {Text("用户名")},
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Person, contentDescription = "", tint = MaterialTheme.colors.primary)
                                    }
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    value = viewModel.registerNicknameText.value,
                                    onValueChange = viewModel::onRegisterNicknameChange,
                                    trailingIcon = {
                                        if (viewModel.registerNicknameText.value.isNotBlank()) {
                                            IconButton(onClick = { viewModel.onRegisterNicknameChange("")  }) {
                                                Icon(imageVector = Icons.Default.Clear, contentDescription = "")
                                            }
                                        }
                                    },
                                    label = {Text("昵称")},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp),
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Face, contentDescription = "", tint = MaterialTheme.colors.primary)
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp),
                                    value = viewModel.registerPasswordText.value,
                                    onValueChange = viewModel::onRegisterPasswordChange,
                                    placeholder = {
                                        Text(text = "8个字符或以上", fontSize = 12.sp)
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { isPasswordVisible2 = !isPasswordVisible2 }) {
                                            Icon(imageVector = if (isPasswordVisible2)Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Password Toggle")
                                        }
                                    },
                                    label = {Text("密码")},
                                    visualTransformation = if (isPasswordVisible2) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.None),
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Lock, contentDescription = "", tint = MaterialTheme.colors.primary)
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp),
                                    value = viewModel.registerPasswordConfirmText.value,
                                    onValueChange = viewModel::onRegisterPasswordConfirmChange,
                                    label = {Text("确认密码")},
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.None),
                                    singleLine = true,
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Password, contentDescription = "", tint = MaterialTheme.colors.primary)
                                    }
                                )
                                Spacer(modifier = Modifier.height(13.dp))
                                Button(
                                    onClick = viewModel::onRegisterClick,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .padding(start = 10.dp, end = 10.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row() {
                                        Text(text = "注册",modifier = Modifier.padding(top=2.dp))
                                        Icon(imageVector = Icons.Default.Launch, contentDescription = "")
                                    }

                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { isVisible = !isVisible }) {
                                    Text(text = "去登录")
                                }
                        }
                    }
                }
            }
        }
    }
}



@Preview
@Composable
fun loginScreenPreview() {

}

