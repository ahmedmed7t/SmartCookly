package com.nexable.smartcookly.feature.favorites.data.repository

import com.nexable.smartcookly.feature.recipes.data.model.CookingStep
import com.nexable.smartcookly.feature.recipes.data.model.Recipe
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.serialization.Serializable

@Serializable
data class FirestoreCookingStep(
    val stepNumber: Int = 0,
    val description: String = "",
    val ingredientsUsed: List<String> = emptyList(),
    val timeMinutes: Int = 0
)

@Serializable
data class FirestoreRecipe(
    val id: String = "",
    val name: String = "",
    val cuisine: String = "",
    val imageUrl: String = "",
    val cookingTimeMinutes: Int = 0,
    val ingredients: List<String> = emptyList(),
    val missingIngredients: List<String> = emptyList(),
    val fitPercentage: Int = 0,
    val rating: Double = 0.0,
    val description: String = "",
    val cookingSteps: List<FirestoreCookingStep> = emptyList()
)

class FavoritesRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val usersCollection = "users"
    private val favoritesSubcollection = "favorites"

    suspend fun addToFavorites(userId: String, recipe: Recipe) {
        try {
            println("FavoritesRepository: Adding recipe ${recipe.id} to favorites for userId: $userId")

            // Convert to serializable format
            val firestoreRecipe = FirestoreRecipe(
                id = recipe.id,
                name = recipe.name,
                cuisine = recipe.cuisine,
                imageUrl = recipe.imageUrl,
                cookingTimeMinutes = recipe.cookingTimeMinutes,
                ingredients = recipe.ingredients,
                missingIngredients = recipe.missingIngredients,
                fitPercentage = recipe.fitPercentage,
                rating = recipe.rating.toDouble(),
                description = recipe.description,
                cookingSteps = recipe.cookingSteps.map { step ->
                    FirestoreCookingStep(
                        stepNumber = step.stepNumber,
                        description = step.description,
                        ingredientsUsed = step.ingredientsUsed,
                        timeMinutes = step.timeMinutes
                    )
                }
            )

            println("FavoritesRepository: Setting document")
            firestore.collection(usersCollection)
                .document(userId)
                .collection(favoritesSubcollection)
                .document(recipe.id)
                .set(firestoreRecipe)
            println("FavoritesRepository: Recipe added to favorites successfully")
        } catch (e: Exception) {
            println("FavoritesRepository: Error adding recipe to favorites - ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun removeFromFavorites(userId: String, recipeId: String) {
        try {
            println("FavoritesRepository: Removing recipe $recipeId from favorites for userId: $userId")
            firestore.collection(usersCollection)
                .document(userId)
                .collection(favoritesSubcollection)
                .document(recipeId)
                .delete()
            println("FavoritesRepository: Recipe removed from favorites successfully")
        } catch (e: Exception) {
            println("FavoritesRepository: Error removing recipe from favorites - ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun getFavorites(userId: String): List<Recipe> {
        return try {
            println("FavoritesRepository: Getting favorites for userId: $userId")
            val snapshot = firestore.collection(usersCollection)
                .document(userId)
                .collection(favoritesSubcollection)
                .get()

            snapshot.documents.mapNotNull { doc ->
                try {
                    // Deserialize to FirestoreRecipe
                    val firestoreRecipe = doc.data<FirestoreRecipe>()
                    
                    // Convert to Recipe
                    Recipe(
                        id = firestoreRecipe.id.ifEmpty { doc.id },
                        name = firestoreRecipe.name,
                        cuisine = firestoreRecipe.cuisine,
                        imageUrl = firestoreRecipe.imageUrl,
                        cookingTimeMinutes = firestoreRecipe.cookingTimeMinutes,
                        ingredients = firestoreRecipe.ingredients,
                        missingIngredients = firestoreRecipe.missingIngredients,
                        fitPercentage = firestoreRecipe.fitPercentage,
                        rating = firestoreRecipe.rating.toFloat(),
                        description = firestoreRecipe.description,
                        cookingSteps = firestoreRecipe.cookingSteps.map { step ->
                            CookingStep(
                                stepNumber = step.stepNumber,
                                description = step.description,
                                ingredientsUsed = step.ingredientsUsed,
                                timeMinutes = step.timeMinutes
                            )
                        }
                    )
                } catch (e: Exception) {
                    println("FavoritesRepository: Error parsing document ${doc.id}: ${e.message}")
                    e.printStackTrace()
                    null
                }
            }
        } catch (e: Exception) {
            println("FavoritesRepository: Error getting favorites - ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun isFavorite(userId: String, recipeId: String): Boolean {
        return try {
            println("FavoritesRepository: Checking if recipe $recipeId is favorite for userId: $userId")
            val document = firestore.collection(usersCollection)
                .document(userId)
                .collection(favoritesSubcollection)
                .document(recipeId)
                .get()
            val exists = document.exists
            println("FavoritesRepository: Recipe is favorite: $exists")
            exists
        } catch (e: Exception) {
            println("FavoritesRepository: Error checking if favorite - ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
