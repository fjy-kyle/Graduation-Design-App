package com.example.socialapplication.presentation.welcome


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.socialapplication.presentation.splash.SplashViewModel
import com.example.socialapplication.ui.theme.Purple700
import com.example.socialapplication.ui.theme.SocialApplicationTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AnimatedSplashScreen(
    navController : NavController,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    var startAnimation by remember {
        mutableStateOf(false)
    }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000
        )
    )
    LaunchedEffect(key1 = true) {
        startAnimation = true

        // 执行耗时逻辑，判断用户之前是否已经登录过
        if (viewModel.isUserLogin()) {
            viewModel.goLogin()
            navController.popBackStack()
            // 登录过则直接到主页
            viewModel.onJoinHome.collectLatest { json->
                navController.navigate("main_screen/$json")
            }
        } else { // 未登录过跳转到登录页面
            navController.popBackStack()
            navController.navigate("login_screen")
        }

 }
    Splash(alpha = alphaAnim.value)
}

@Composable
fun Splash(alpha : Float) {
    Box(
        modifier = Modifier
            .background(if (isSystemInDarkTheme()) Color.Black else Purple700)
            .fillMaxSize(),
        contentAlignment = Alignment.Center

    ) {
        Icon(
            modifier = Modifier
                .size(120.dp)
                .alpha(alpha = alpha),
            imageVector = Icons.Default.SupervisorAccount,
            contentDescription = "Logo Icon",
            tint = Color.White
        )
    }
}

@Preview
@Composable
fun splashPreview(){
    SocialApplicationTheme {
        Splash(alpha = 1f)
    }
}