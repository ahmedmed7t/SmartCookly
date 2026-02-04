package com.nexable.smartcookly.navigation

import com.nexable.smartcookly.feature.recipes.data.model.CookingStep
import com.nexable.smartcookly.feature.recipes.data.model.Recipe

object QuickMealsCache {
    private var cachedRecipes: List<Recipe>? = null
    private val cachedCookingSteps: MutableMap<String, List<CookingStep>> = mutableMapOf()
    
    fun storeRecipes(recipes: List<Recipe>) {
        cachedRecipes = recipes
    }
    
    fun getRecipes(): List<Recipe>? {
        return cachedRecipes
    }
    
    fun storeCookingSteps(recipeId: String, steps: List<CookingStep>) {
        cachedCookingSteps[recipeId] = steps
    }
    
    fun getCookingSteps(recipeId: String): List<CookingStep>? {
        return cachedCookingSteps[recipeId]
    }
    
    fun clearCache() {
        cachedRecipes = null
        cachedCookingSteps.clear()
    }
}
