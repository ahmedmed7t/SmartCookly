package com.nexable.smartcookly.navigation

import com.nexable.smartcookly.feature.recipes.data.model.Recipe

object FavoriteRecipeCache {
    private var cachedRecipe: Recipe? = null
    
    fun storeRecipe(recipe: Recipe) {
        cachedRecipe = recipe
    }
    
    fun getRecipe(): Recipe? {
        return cachedRecipe
    }
    
    fun clearRecipe() {
        cachedRecipe = null
    }
}
