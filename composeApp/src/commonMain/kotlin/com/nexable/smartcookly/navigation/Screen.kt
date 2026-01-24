package com.nexable.smartcookly.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Fridge : Screen("fridge")
    data object Favorites : Screen("favorites")
    data object Shopping : Screen("shopping")
    data object ReviewScan : Screen("review_scan")
    data object Profile : Screen("profile")
}
