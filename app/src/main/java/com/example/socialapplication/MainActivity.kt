package com.example.socialapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.Post
import com.example.socialapplication.domain.model.User
import com.example.socialapplication.presentation.home.MainScreen
import com.example.socialapplication.presentation.home.home.PostEditScreen
import com.example.socialapplication.presentation.home.postDetail.PostDetailScreen
import com.example.socialapplication.presentation.welcome.AnimatedSplashScreen
import com.example.socialapplication.presentation.welcome.LoginScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onStart() {
        super.onStart()
        Log.d("onStart","you are in onStart!!!")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "splash_screen"
            ) {

                // 开屏动画
                composable("splash_screen") {
                    AnimatedSplashScreen(navController)
                }
                // 登录与注册页面
                composable("login_screen") {
                    LoginScreen(onNavigate = navController::navigate)
                }
                // 帖子编辑页面
                composable("postEdit_screen") {
                    PostEditScreen(onNavigate = navController::popBackStack)
                }
                // 主页
                composable(
                    "main_screen/{user}",
                    arguments = listOf(
                        navArgument("user") {
                            type = NavType.StringType
                            nullable = false
                        }
                    ),
                ) {
                    val argument = requireNotNull(it.arguments)
                    val json = argument.getString("user")
                    val user = Json.decodeFromString<User>(json!!)
                    MainScreen(user, navController)
                }
                composable("postDetail_screen/{post}",
                    arguments = listOf(
                        navArgument("post") {
                            type = NavType.StringType
                            nullable = false
                        }
                    ),
                ){
                    val argument = requireNotNull(it.arguments)
                    val json = argument.getString("post")
                    val post = Json.decodeFromString<Post>(json!!)
                    PostDetailScreen(
                        post = post,
                        navController = navController
                    )
                }
            }
        }
    }
}

