package com.example.socialapplication.presentation.home

import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.socialapplication.ui.theme.Purple200
import io.ktor.util.reflect.*

@Composable
fun BottomBarView(navController: NavController) {
    val navItems = listOf(
        BottomItemScreen.Home,
        BottomItemScreen.ChatRoom,
        BottomItemScreen.Mine,
    )
    val navBackStackEntry by  navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    BottomAppBar() {
        navItems.forEach{
            BottomNavigationItem(
                label = { Text(text = it.title)},
                icon = { Icon(it.icon, contentDescription = "") },
                selectedContentColor = Color.White,
                unselectedContentColor = Purple200,
                selected = currentRoute == it.route,
                onClick = {
                    navController.navigate(it.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) 
        }
    }
}