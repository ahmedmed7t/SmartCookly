package com.nexable.smartcookly.feature.shopping.data.repository

import com.nexable.smartcookly.feature.shopping.data.model.ShoppingItem
import com.nexable.smartcookly.feature.shopping.data.model.Urgency
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.serialization.Serializable

@Serializable
data class FirestoreShoppingItem(
    val id: String = "",
    val name: String = "",
    val urgency: Urgency = Urgency.NORMAL,
    val addedAt: Long = 0
)

class ShoppingRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val usersCollection = "users"
    private val shoppingListSubcollection = "shoppingList"

    suspend fun addItem(userId: String, item: ShoppingItem) {
        try {
            println("ShoppingRepository: Adding item ${item.name} to shopping list for userId: $userId")
            
            val itemId = if (item.id.isNotEmpty()) item.id else java.util.UUID.randomUUID().toString()
            
            val firestoreItem = FirestoreShoppingItem(
                id = itemId,
                name = item.name,
                urgency = item.urgency,
                addedAt = if (item.addedAt > 0) item.addedAt else System.currentTimeMillis()
            )

            firestore.collection(usersCollection)
                .document(userId)
                .collection(shoppingListSubcollection)
                .document(itemId)
                .set(firestoreItem)
            println("ShoppingRepository: Item added to shopping list successfully")
        } catch (e: Exception) {
            println("ShoppingRepository: Error adding item to shopping list - ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun getItems(userId: String): List<ShoppingItem> {
        return try {
            println("ShoppingRepository: Getting shopping list for userId: $userId")
            val snapshot = firestore.collection(usersCollection)
                .document(userId)
                .collection(shoppingListSubcollection)
                .get()

            snapshot.documents.mapNotNull { doc ->
                try {
                    val firestoreItem = doc.data<FirestoreShoppingItem>()
                    
                    ShoppingItem(
                        id = firestoreItem.id.ifEmpty { doc.id },
                        name = firestoreItem.name,
                        urgency = firestoreItem.urgency,
                        addedAt = firestoreItem.addedAt
                    )
                } catch (e: Exception) {
                    println("ShoppingRepository: Error parsing document ${doc.id}: ${e.message}")
                    e.printStackTrace()
                    null
                }
            }
        } catch (e: Exception) {
            println("ShoppingRepository: Error getting shopping list - ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun itemExists(userId: String, itemName: String): Boolean {
        return try {
            println("ShoppingRepository: Checking if item '$itemName' exists for userId: $userId")
            val snapshot = firestore.collection(usersCollection)
                .document(userId)
                .collection(shoppingListSubcollection)
                .get()

            val exists = snapshot.documents.any { doc ->
                try {
                    val firestoreItem = doc.data<FirestoreShoppingItem>()
                    firestoreItem.name.equals(itemName, ignoreCase = true)
                } catch (e: Exception) {
                    false
                }
            }
            println("ShoppingRepository: Item '$itemName' exists: $exists")
            exists
        } catch (e: Exception) {
            println("ShoppingRepository: Error checking if item exists - ${e.message}")
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteItem(userId: String, itemId: String) {
        try {
            println("ShoppingRepository: Deleting item $itemId from shopping list for userId: $userId")
            firestore.collection(usersCollection)
                .document(userId)
                .collection(shoppingListSubcollection)
                .document(itemId)
                .delete()
            println("ShoppingRepository: Item deleted successfully")
        } catch (e: Exception) {
            println("ShoppingRepository: Error deleting item - ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun deleteAllItems(userId: String) {
        try {
            println("ShoppingRepository: Deleting all items from shopping list for userId: $userId")
            val snapshot = firestore.collection(usersCollection)
                .document(userId)
                .collection(shoppingListSubcollection)
                .get()

            snapshot.documents.forEach { doc ->
                doc.reference.delete()
            }
            println("ShoppingRepository: All items deleted successfully")
        } catch (e: Exception) {
            println("ShoppingRepository: Error deleting all items - ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}
