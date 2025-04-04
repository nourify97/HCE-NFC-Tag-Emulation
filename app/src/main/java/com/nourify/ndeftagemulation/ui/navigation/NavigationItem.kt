package com.nourify.ndeftagemulation.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
)

val navigationItems =
    listOf(
        NavigationItem(
            title = "Home",
            icon = Icons.Default.Home,
            route = Screen.Home.route,
        ),
        NavigationItem(
            title = "TagList",
            icon = Icons.Default.Favorite,
            route = Screen.TagList.route,
        ),
    )

sealed class Screen(
    val route: String,
) {
    data object Home : Screen("home_screen")

    data object TagList : Screen("taglist_screen")
}
