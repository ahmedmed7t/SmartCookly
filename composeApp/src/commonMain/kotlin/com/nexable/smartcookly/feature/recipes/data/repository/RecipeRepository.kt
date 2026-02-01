package com.nexable.smartcookly.feature.recipes.data.repository

import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import com.nexable.smartcookly.feature.fridge.data.remote.OpenAIApiClient
import com.nexable.smartcookly.feature.onboarding.data.model.CookingLevel
import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.onboarding.data.model.DietaryStyle
import com.nexable.smartcookly.feature.onboarding.data.model.DislikedIngredient
import com.nexable.smartcookly.feature.onboarding.data.model.Ingredient
import com.nexable.smartcookly.feature.recipes.data.model.CookingStep
import com.nexable.smartcookly.feature.recipes.data.model.Recipe
import com.nexable.smartcookly.feature.recipes.data.remote.PexelsApiClient
import com.nexable.smartcookly.feature.recipes.presentation.DiscoveryMode
import com.nexable.smartcookly.netwrokUtils.NetworkError
import com.nexable.smartcookly.netwrokUtils.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class RecipeRepository(
    private val openAIApiClient: OpenAIApiClient,
    private val pexelsApiClient: PexelsApiClient
) {
    suspend fun discoverRecipes(
        discoveryMode: DiscoveryMode,
        cuisines: Set<Cuisine>,
        fridgeItems: List<FridgeItem>,
        dietaryStyle: DietaryStyle?,
        avoidedIngredients: Set<Ingredient>,
        dislikedIngredients: Set<DislikedIngredient>,
        cookingLevel: CookingLevel?
    ): Result<List<Recipe>, NetworkError> {
        // First, get recipes from OpenAI
        val recipesResult = openAIApiClient.discoverRecipes(
            discoveryMode = discoveryMode,
            cuisines = cuisines,
            fridgeItems = fridgeItems,
            dietaryStyle = dietaryStyle,
            avoidedIngredients = avoidedIngredients,
            dislikedIngredients = dislikedIngredients,
            cookingLevel = cookingLevel
        )
        
        // If successful, fetch images from Pexels for each recipe
        return when (recipesResult) {
            is Result.Success -> {
                val recipesWithImages = fetchImagesForRecipes(recipesResult.data)
                Result.Success(recipesWithImages)
            }
            is Result.Error -> recipesResult
        }
    }
    
    private suspend fun fetchImagesForRecipes(recipes: List<Recipe>): List<Recipe> = coroutineScope {
        // Fetch images in parallel for better performance
        val deferredRecipes = recipes.map { recipe ->
            async {
                val imageResult = pexelsApiClient.searchFoodImage(recipe.name)
                when (imageResult) {
                    is Result.Success -> {
                        if (imageResult.data.isNotEmpty()) {
                            recipe.copy(imageUrl = imageResult.data)
                        } else {
                            recipe
                        }
                    }
                    is Result.Error -> {
                        println("RecipeRepository: Failed to fetch image for '${recipe.name}'")
                        recipe
                    }
                }
            }
        }
        
        deferredRecipes.awaitAll()
    }
    
    suspend fun getCookingSteps(
        recipeName: String,
        ingredients: List<String>
    ): Result<List<CookingStep>, NetworkError> {
        return openAIApiClient.fetchCookingSteps(recipeName, ingredients)
    }
}
