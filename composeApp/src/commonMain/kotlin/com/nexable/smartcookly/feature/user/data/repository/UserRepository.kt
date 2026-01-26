package com.nexable.smartcookly.feature.user.data.repository

import com.nexable.smartcookly.feature.user.data.model.UserProfile
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.Timestamp

class UserRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val usersCollection = "users"
    
    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val document = firestore.collection(usersCollection)
                .document(userId)
                .get()
            
            if (document.exists) {
                val data = document.data<Map<String, Any?>>()
                fun getStringOrNull(key: String): String? {
                    val value = data[key] as? String
                    return value?.takeIf { it.isNotEmpty() }
                }
                
                UserProfile(
                    cuisines = (data["cuisines"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    otherCuisineText = getStringOrNull("otherCuisineText"),
                    dietaryStyle = getStringOrNull("dietaryStyle"),
                    otherDietaryStyleText = getStringOrNull("otherDietaryStyleText"),
                    avoidedIngredients = (data["avoidedIngredients"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    otherIngredientText = getStringOrNull("otherIngredientText"),
                    dislikedIngredients = (data["dislikedIngredients"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    otherDislikedIngredientText = getStringOrNull("otherDislikedIngredientText"),
                    diseases = (data["diseases"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    otherDiseaseText = getStringOrNull("otherDiseaseText"),
                    cookingLevel = getStringOrNull("cookingLevel"),
                    createdAt = data["createdAt"] as? Timestamp,
                    updatedAt = data["updatedAt"] as? Timestamp
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun saveUserProfile(userId: String, profile: UserProfile) {
        try {
            println("UserRepository: Saving profile for userId: $userId")
            val now = Timestamp.now()
            val profileToSave = if (profile.createdAt == null) {
                profile.copy(createdAt = now, updatedAt = now)
            } else {
                profile.copy(updatedAt = now)
            }
            
            val data = mapOf(
                "cuisines" to profileToSave.cuisines,
                "otherCuisineText" to (profileToSave.otherCuisineText ?: ""),
                "dietaryStyle" to (profileToSave.dietaryStyle ?: ""),
                "otherDietaryStyleText" to (profileToSave.otherDietaryStyleText ?: ""),
                "avoidedIngredients" to profileToSave.avoidedIngredients,
                "otherIngredientText" to (profileToSave.otherIngredientText ?: ""),
                "dislikedIngredients" to profileToSave.dislikedIngredients,
                "otherDislikedIngredientText" to (profileToSave.otherDislikedIngredientText ?: ""),
                "diseases" to profileToSave.diseases,
                "otherDiseaseText" to (profileToSave.otherDiseaseText ?: ""),
                "cookingLevel" to (profileToSave.cookingLevel ?: ""),
                "createdAt" to profileToSave.createdAt,
                "updatedAt" to profileToSave.updatedAt
            )
            
            println("UserRepository: Setting document with data: $data")
            firestore.collection(usersCollection)
                .document(userId)
                .set(data)
            println("UserRepository: Document set successfully")
        } catch (e: Exception) {
            println("UserRepository: Error saving profile - ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    suspend fun userExists(userId: String): Boolean {
        return try {
            println("UserRepository: Checking if user exists: $userId")
            val document = firestore.collection(usersCollection)
                .document(userId)
                .get()
            val exists = document.exists
            println("UserRepository: User exists: $exists")
            exists
        } catch (e: Exception) {
            println("UserRepository: Error checking user existence - ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
