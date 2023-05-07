package com.example.socialapplication.presentation.home


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chat_app.presentation.chat.ChatScreen
import com.example.socialapplication.domain.model.User
import com.example.socialapplication.presentation.home.home.HomeScreen
import com.example.socialapplication.presentation.home.mine.MineScreen
import com.example.socialapplication.ui.theme.SocialApplicationTheme


@Composable
fun MainScreen(
    user: User,
    login_navController: NavController,
){


    Surface(
        color = MaterialTheme.colors.primary
    ) {
        val navController = rememberNavController()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomBarView(navController = navController)
            }
        ) { PaddingValues ->
            NavHost(
                modifier = Modifier.padding(PaddingValues),
                navController = navController,
                startDestination = BottomItemScreen.Home.route
            ){
                composable(BottomItemScreen.Home.route){
                    HomeScreen(
                        onNavigate = login_navController::navigate,
                        navController = login_navController
                    )
                }
                // 聊天页面
                composable(BottomItemScreen.ChatRoom.route) {
                    ChatScreen()
                }
                composable(BottomItemScreen.Mine.route) {
                    MineScreen(onNavigate = login_navController::navigate, user = user)
                }

            }
        }
    }

}



@Preview
@Composable
fun homeScreenPreview(){
    SocialApplicationTheme() {
    }
}


