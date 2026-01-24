package com.nexable.smartcookly.feature.fridge.data.remote

import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import com.nexable.smartcookly.feature.fridge.data.remote.dto.Content
import com.nexable.smartcookly.feature.fridge.data.remote.dto.ImageAnalysisRequest
import com.nexable.smartcookly.feature.fridge.data.remote.dto.ImageAnalysisResponse
import com.nexable.smartcookly.feature.fridge.data.remote.dto.ImageUrl
import com.nexable.smartcookly.feature.fridge.data.remote.dto.Message
import com.nexable.smartcookly.netwrokUtils.BaseNetworkClient
import com.nexable.smartcookly.netwrokUtils.NetworkError
import com.nexable.smartcookly.netwrokUtils.Result
import com.nexable.smartcookly.netwrokUtils.map
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class OpenAIApiClient(
    httpClient: HttpClient,
    private val apiKey: String
) : BaseNetworkClient(httpClient) {

    companion object {
        private const val BASE_URL = "https://api.openai.com/v1"
        private const val CHAT_COMPLETIONS_ENDPOINT = "$BASE_URL/chat/completions"
        
        private val PROMPT = """
            Analyze this image and identify all food items visible. 
            For each item, return a JSON array with objects containing:
            - name: the food item name
            - estimated_quantity: number of items/pieces visible or null
            - unit: unit of measurement (g, pcs, tub, bottle, etc.) or null
            - category: one of VEGETABLES, DAIRY, PROTEINS, FRUITS, GRAINS, OTHER
            - estimated_days_until_expiration: estimated days until expiration (or null if unknown)
            
            Return ONLY a valid JSON array, no other text. Example format:
            [
              {"name": "Spinach", "estimated_quantity": 1, "unit": "bunch", "category": "VEGETABLES", "estimated_days_until_expiration": 5},
              {"name": "Whole Milk", "estimated_quantity": 2, "unit": "bottle", "category": "DAIRY", "estimated_days_until_expiration": 3}
            ]
            if you could not recognize any food just return an empty list and don't reply with anything else other 
            than food
        """.trimIndent()
    }

    suspend fun analyzeImage(imageBase64: String): Result<List<FridgeItem>, NetworkError> {
        val imageUrl = "data:image/jpeg;base64,$imageBase64"
        
        val request = ImageAnalysisRequest(
            model = "gpt-4o",
            messages = listOf(
                Message(
                    role = "user",
                    content = listOf(
                        Content(
                            type = "text",
                            text = PROMPT
                        ),
                        Content(
                            type = "image_url",
                            imageUrl = ImageUrl(
                                url = imageUrl
                            )
                        )
                    )
                )
            ),
            max_tokens = 2000
        )

        return post<ImageAnalysisResponse>(CHAT_COMPLETIONS_ENDPOINT) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.map { response ->
            parseDetectedItems(response)
        }
    }

    private fun parseDetectedItems(response: ImageAnalysisResponse): List<FridgeItem> {
        val content = response.choices.firstOrNull()?.message?.content ?: return emptyList()
        
        return try {
            // Try to extract JSON array from the response
            val jsonContent = content.trim()
            val jsonStart = jsonContent.indexOf('[')
            val jsonEnd = jsonContent.lastIndexOf(']') + 1
            
            if (jsonStart == -1 || jsonEnd == 0) {
                return emptyList()
            }
            
            val jsonArrayString = jsonContent.substring(jsonStart, jsonEnd)
            val json = Json { ignoreUnknownKeys = true }
            val items = json.parseToJsonElement(jsonArrayString).jsonArray
            
            items.mapNotNull { item ->
                val obj = item.jsonObject
                val name = obj["name"]?.jsonPrimitive?.content ?: return@mapNotNull null
                val categoryStr = obj["category"]?.jsonPrimitive?.content ?: "OTHER"
                val daysUntilExpiration = obj["estimated_days_until_expiration"]?.jsonPrimitive?.content?.toIntOrNull()
                
                val category = try {
                    FoodCategory.valueOf(categoryStr.uppercase())
                } catch (e: IllegalArgumentException) {
                    FoodCategory.OTHER
                }
                
                val expirationDate = daysUntilExpiration?.let { days ->
                    val today = Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    today.plus(days, DateTimeUnit.DAY)
                }
                
                FridgeItem(
                    name = name,
                    category = category,
                    expirationDate = expirationDate
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
