package com.nexable.smartcookly.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object LoginEncouragement : Screen("login_encouragement")
    data object Login : Screen("login")
    data object SignUp : Screen("signup")
    data object App : Screen("app")
    data object Home : Screen("home")
    data object Fridge : Screen("fridge")
    data object Favorites : Screen("favorites")
    data object Shopping : Screen("shopping")
    data object ReviewScan : Screen("review_scan")
    data object Profile : Screen("profile")
    data object EditCuisines : Screen("edit_cuisines")
    data object EditDietary : Screen("edit_dietary")
    data object EditAllergies : Screen("edit_allergies")
    data object EditDisliked : Screen("edit_disliked")
    data object EditHealth : Screen("edit_health")
    data object EditCookingLevel : Screen("edit_cooking_level")
}
