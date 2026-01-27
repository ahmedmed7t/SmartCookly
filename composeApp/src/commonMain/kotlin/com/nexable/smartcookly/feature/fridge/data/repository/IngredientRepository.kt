package com.nexable.smartcookly.feature.fridge.data.repository

import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

class IngredientRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val usersCollection = "users"
    private val ingredientsSubcollection = "ingredients"
    
    suspend fun addIngredient(userId: String, item: FridgeItem) {
        try {
            println("IngredientRepository: Adding ingredient for userId: $userId")
            val now = Timestamp.now()
            
            val data = mutableMapOf<String, Any?>(
                "id" to item.id,
                "name" to item.name,
                "category" to item.category.name,
                "createdAt" to now,
                "updatedAt" to now
            )
            
            // Add expiration date if present
            item.expirationDate?.let { date ->
                val instant = date.atStartOfDayIn(TimeZone.currentSystemDefault())
                // Convert Instant to Timestamp using seconds and nanoseconds
                val seconds = instant.epochSeconds
                val nanoseconds = instant.nanosecondsOfSecond
                data["expirationDate"] = Timestamp(seconds, nanoseconds)
            } ?: run {
                data["expirationDate"] = null
            }
            
            // Add imageUrl if present
            item.imageUrl?.let { url ->
                data["imageUrl"] = url
            } ?: run {
                data["imageUrl"] = ""
            }
            
            // Add freshStatus
            data["freshStatus"] = item.freshStatus.name
            
            println("IngredientRepository: Setting document with data: $data")
            firestore.collection(usersCollection)
                .document(userId)
                .collection(ingredientsSubcollection)
                .document(item.id)
                .set(data)
            println("IngredientRepository: Document set successfully")
        } catch (e: Exception) {
            println("IngredientRepository: Error adding ingredient - ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    suspend fun getIngredients(userId: String): List<FridgeItem> {
        return try {
            println("IngredientRepository: Getting ingredients for userId: $userId")
            val snapshot = firestore.collection(usersCollection)
                .document(userId)
                .collection(ingredientsSubcollection)
                .get()
            
            snapshot.documents.mapNotNull { doc ->
                try {
                    // Access fields individually using get() to avoid serialization issues
                    val name = doc.get("name") as? String ?: return@mapNotNull null
                    val categoryStr = doc.get("category") as? String ?: return@mapNotNull null
                    val expirationTimestamp = doc.get("expirationDate") as? Timestamp?
                    val imageUrl = doc.get("imageUrl") as? String?
                    val freshStatusStr = doc.get("freshStatus") as? String? ?: "GOOD"
                    
                    val category = try {
                        com.nexable.smartcookly.feature.fridge.data.model.FoodCategory.valueOf(categoryStr)
                    } catch (e: IllegalArgumentException) {
                        com.nexable.smartcookly.feature.fridge.data.model.FoodCategory.OTHER
                    }
                    
                    val expirationDate = expirationTimestamp?.let { ts ->
                        // Convert Timestamp to Instant, then to LocalDate
                        val instant = Instant.fromEpochSeconds(ts.seconds, ts.nanoseconds)
                        instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    }
                    
                    val freshStatus = try {
                        com.nexable.smartcookly.feature.fridge.data.model.FreshStatus.valueOf(freshStatusStr)
                    } catch (e: IllegalArgumentException) {
                        com.nexable.smartcookly.feature.fridge.data.model.FreshStatus.GOOD
                    }
                    
                    FridgeItem(
                        id = doc.id,
                        name = name,
                        category = category,
                        expirationDate = expirationDate,
                        imageUrl = imageUrl?.takeIf { it.isNotEmpty() },
                        freshStatus = freshStatus
                    )
                } catch (e: Exception) {
                    println("IngredientRepository: Error parsing document ${doc.id}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            println("IngredientRepository: Error getting ingredients - ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}
