package com.nexable.smartcookly.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object LoginEncouragement : Screen("login_encouragement")
    data object Login : Screen("login")
    data object SignUp : Screen("signup")
    data object App : Screen("app")
    data object Home : Screen("home")
    data object Fridge : Screen("fridge")
    data object Recipes : Screen("recipes")
    data object Shopping : Screen("shopping")
    data object ReviewScan : Screen("review_scan")
    data object Profile : Screen("profile")
    data object EditCuisines : Screen("edit_cuisines")
    data object EditDietary : Screen("edit_dietary")
    data object EditAllergies : Screen("edit_allergies")
    data object EditDisliked : Screen("edit_disliked")
    data object EditHealth : Screen("edit_health")
    data object EditCookingLevel : Screen("edit_cooking_level")
    data object AddIngredient : Screen("add_ingredient")
    data object AddShoppingItem : Screen("add_shopping_item")
    data object DiscoverRecipes : Screen("discover_recipes")
    data object Favorites : Screen("favorites")
    data object FavoriteRecipeFlow : Screen("favorite_recipe_flow")
    
    // Sub-navigation routes for ReviewScan flow
    sealed class ReviewScanSubScreen(route: String) : Screen(route) {
        data object ReviewScanList : ReviewScanSubScreen("review_scan_list")
        data object ModifyIngredient : ReviewScanSubScreen("modify_ingredient/{itemId}") {
            fun createRoute(itemId: String) = "modify_ingredient/$itemId"
        }
    }
    
    // Sub-navigation routes for DiscoverRecipes flow
    sealed class DiscoverRecipesSubScreen(route: String) : Screen(route) {
        data object DiscoverRecipesList : DiscoverRecipesSubScreen("discover_recipes_list")
        data object RecipeDetails : DiscoverRecipesSubScreen("recipe_details/{recipeId}") {
            fun createRoute(recipeId: String) = "recipe_details/$recipeId"
        }
        data object CookingMode : DiscoverRecipesSubScreen("cooking_mode/{recipeId}") {
            fun createRoute(recipeId: String) = "cooking_mode/$recipeId"
        }
    }
}
