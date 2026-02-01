package com.nexable.smartcookly.feature.fridge.data.remote

import com.nexable.smartcookly.feature.fridge.data.model.FoodCategory
import com.nexable.smartcookly.feature.fridge.data.model.FridgeItem
import com.nexable.smartcookly.feature.fridge.data.remote.dto.Content
import com.nexable.smartcookly.feature.fridge.data.remote.dto.ImageAnalysisRequest
import com.nexable.smartcookly.feature.fridge.data.remote.dto.ImageAnalysisResponse
import com.nexable.smartcookly.feature.fridge.data.remote.dto.ImageUrl
import com.nexable.smartcookly.feature.fridge.data.remote.dto.Message
import com.nexable.smartcookly.feature.onboarding.data.model.CookingLevel
import com.nexable.smartcookly.feature.onboarding.data.model.Cuisine
import com.nexable.smartcookly.feature.onboarding.data.model.DislikedIngredient
import com.nexable.smartcookly.feature.onboarding.data.model.DietaryStyle
import com.nexable.smartcookly.feature.onboarding.data.model.Ingredient
import com.nexable.smartcookly.feature.recipes.data.model.Recipe
import com.nexable.smartcookly.feature.recipes.presentation.DiscoveryMode
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

        private val SYSTEM_PROMPT = """
            
            You are an expert food recognition and inventory assistant AI.

            Your task is to analyze food images and detect all visible food items and ingredients as accurately as possible.

            For each detected item, you must:
            - Identify the ingredient name clearly and in a standardized form (e.g., "chicken breast", "tomato", "cheddar cheese").
            - Assign a suitable food category (e.g., Vegetables, Fruits, Dairy, Proteins, Grains, Condiments, Beverages, Frozen Foods, Snacks, Other).
            - Estimate an expected expiration date based on:
              - The visual condition of the item (freshness, ripeness, visible spoilage, packaging state).
              - Whether the item appears fresh, opened, sealed, frozen, or packaged.
              - Typical shelf life standards for that type of food.

            Rules and constraints:
            - If an expiration date is printed on visible packaging, always use it.
            - If no printed date is visible, provide a reasonable estimated expiration date in ISO format (YYYY-MM-DD) and include an "expiration_confidence" score from 0 to 100.
            - If you are unsure about an item, still include it but mark it with a low confidence score.
            - Never hallucinate branded expiration dates or exact storage history.
            - Be conservative: prefer shorter shelf life estimates if freshness is unclear.
            - Do not include non-food objects.

            Output requirements:
            - Return valid JSON only.
            - The output must be a JSON array.
            - Each object must include exactly these fields:
               - name: the food item name
                - category: one of VEGETABLES, DAIRY, PROTEINS, FRUITS, GRAINS, OTHER
                - estimated_days_until_expiration: estimated days until expiration (or null if unknown)
            
            Return ONLY a valid JSON array, no other text. Example format:
            [
              {"name": "Spinach", "category": "VEGETABLES", "estimated_days_until_expiration": 5},
              {"name": "Whole Milk", "category": "DAIRY", "estimated_days_until_expiration": 3}
            ]
            if you could not recognize any food just return an empty list and don't reply with anything else other 
            than food

        """.trimIndent()
    }

    suspend fun analyzeImageFromUrl(imageUrl: String): Result<List<FridgeItem>, NetworkError> {
        val request = ImageAnalysisRequest(
            model = "gpt-4o-mini",
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
                ),
                Message(
                    role = "system",
                    content = listOf(
                        Content(
                            type = "text",
                            text = SYSTEM_PROMPT
                        ),
                    )
                )
            ),
            max_completion_tokens = 2000
        )

        return post<ImageAnalysisResponse>(CHAT_COMPLETIONS_ENDPOINT) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.map { response ->
            parseDetectedItems(response)
        }
    }
    
    suspend fun analyzeImage(imageBase64: String): Result<List<FridgeItem>, NetworkError> {
        val imageUrl = "data:image/jpeg;base64,$imageBase64"
        return analyzeImageFromUrl(imageUrl)
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
                
                val category = FoodCategory.fromLegacyValue(categoryStr)
                
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
    
    suspend fun discoverRecipes(
        discoveryMode: DiscoveryMode,
        cuisines: Set<Cuisine>,
        fridgeItems: List<FridgeItem>,
        dietaryStyle: DietaryStyle?,
        avoidedIngredients: Set<Ingredient>,
        dislikedIngredients: Set<DislikedIngredient>,
        cookingLevel: CookingLevel?
    ): Result<List<Recipe>, NetworkError> {
        println("OpenAIApiClient: Building recipe discovery prompt...")
        
        val prompt = buildRecipeDiscoveryPrompt(
            discoveryMode = discoveryMode,
            cuisines = cuisines,
            fridgeItems = fridgeItems,
            dietaryStyle = dietaryStyle,
            avoidedIngredients = avoidedIngredients,
            dislikedIngredients = dislikedIngredients,
            cookingLevel = cookingLevel
        )
        
        println("OpenAIApiClient: Prompt length = ${prompt.length} chars")
        
        val request = ImageAnalysisRequest(
            model = "gpt-4o-mini",
            messages = listOf(
                Message(
                    role = "user",
                    content = listOf(
                        Content(
                            type = "text",
                            text = prompt
                        )
                    )
                )
            ),
            max_completion_tokens = 4000
        )
        
        println("OpenAIApiClient: Making API request to OpenAI...")
        
        return post<ImageAnalysisResponse>(CHAT_COMPLETIONS_ENDPOINT) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.also { result ->
            when (result) {
                is Result.Success -> println("OpenAIApiClient: API request succeeded")
                is Result.Error -> println("OpenAIApiClient: API request failed with ${result.error}")
            }
        }.map { response ->
            println("OpenAIApiClient: Parsing recipes from response...")
            val recipes = parseRecipes(response, fridgeItems)
            println("OpenAIApiClient: Parsed ${recipes.size} recipes")
            recipes
        }
    }
    
    private fun buildRecipeDiscoveryPrompt(
        discoveryMode: DiscoveryMode,
        cuisines: Set<Cuisine>,
        fridgeItems: List<FridgeItem>,
        dietaryStyle: DietaryStyle?,
        avoidedIngredients: Set<Ingredient>,
        dislikedIngredients: Set<DislikedIngredient>,
        cookingLevel: CookingLevel?
    ): String {
        val cuisineList = if (cuisines.isEmpty()) "Any" else cuisines.joinToString(", ") { it.displayName }
        val fridgeItemsList = if (fridgeItems.isEmpty()) "None specified" else fridgeItems.joinToString(", ") { it.name }
        val avoidedList = if (avoidedIngredients.isEmpty()) "None" else avoidedIngredients.joinToString(", ") { it.displayName }
        val dislikedList = if (dislikedIngredients.isEmpty()) "None" else dislikedIngredients.joinToString(", ") { it.displayName }
        
        val modeDescription = when (discoveryMode) {
            DiscoveryMode.PREFERENCES ->
                "Suggest recipes based on user preferences and cuisines."
            DiscoveryMode.FRIDGE ->
                "Suggest recipes using ingredients available in the fridge."
            DiscoveryMode.BOTH ->
                "Suggest recipes that use fridge ingredients AND match preferences."
        }
        
        return """
            Suggest up to 5 recipes. $modeDescription
            
            Preferences:
            - Dietary: ${dietaryStyle?.displayName ?: "Any"}
            - Avoid: $avoidedList
            - Dislike: $dislikedList
            - Skill: ${cookingLevel?.displayName ?: "Any"}
            
            Fridge: $fridgeItemsList
            Cuisines: $cuisineList
            
            Return ONLY a JSON array:
            [{"name":"Dish Name","image_url":"image url you suggest from network","cuisine":"Italian","cooking_time_minutes":30,"ingredients":["item1","item2"],"fit_percentage":85,"rating":4.5,"description":"Brief description"}]
        """.trimIndent()
    }
    
    private fun parseRecipes(
        response: ImageAnalysisResponse,
        fridgeItems: List<FridgeItem>
    ): List<Recipe> {
        val content = response.choices.firstOrNull()?.message?.content
        
        if (content == null) {
            println("OpenAIApiClient: No content in response")
            return emptyList()
        }
        
        println("OpenAIApiClient: Response content length = ${content.length}")
        
        return try {
            val jsonContent = content.trim()
            val jsonStart = jsonContent.indexOf('[')
            val jsonEnd = jsonContent.lastIndexOf(']') + 1
            
            if (jsonStart == -1 || jsonEnd == 0) {
                println("OpenAIApiClient: No JSON array found in response")
                println("OpenAIApiClient: Content = $jsonContent")
                return emptyList()
            }
            
            val jsonArrayString = jsonContent.substring(jsonStart, jsonEnd)
            println("OpenAIApiClient: Extracted JSON array of length ${jsonArrayString.length}")
            
            val json = Json { ignoreUnknownKeys = true }
            val items = json.parseToJsonElement(jsonArrayString).jsonArray
            
            println("OpenAIApiClient: Found ${items.size} items in JSON array")
            
            val fridgeItemNames = fridgeItems.map { it.name.lowercase() }.toSet()
            
            items.mapIndexedNotNull { index, item ->
                try {
                    val obj = item.jsonObject
                    val name = obj["name"]?.jsonPrimitive?.content ?: return@mapIndexedNotNull null
                    val cuisine = obj["cuisine"]?.jsonPrimitive?.content ?: "Unknown"
                    val imageUrl = obj["image_url"]?.jsonPrimitive?.content ?: ""
                    val cookingTime = obj["cooking_time_minutes"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
                    val ingredientsArray = obj["ingredients"]?.jsonArray
                    val ingredients = ingredientsArray?.mapNotNull { it.jsonPrimitive.content } ?: emptyList()
                    val fitPercentage = obj["fit_percentage"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
                    val rating = obj["rating"]?.jsonPrimitive?.content?.toFloatOrNull() ?: 0f
                    val description = obj["description"]?.jsonPrimitive?.content ?: ""
                    
                    // Calculate missing ingredients
                    val missingIngredients = ingredients.filter { ingredient ->
                        !fridgeItemNames.any { fridgeName ->
                            ingredient.lowercase().contains(fridgeName) || fridgeName.contains(ingredient.lowercase())
                        }
                    }
                    
                    println("OpenAIApiClient: Parsed recipe $index: $name")
                    
                    Recipe(
                        id = "recipe_$index",
                        name = name,
                        cuisine = cuisine,
                        imageUrl = imageUrl,
                        cookingTimeMinutes = cookingTime,
                        ingredients = ingredients,
                        missingIngredients = missingIngredients,
                        fitPercentage = fitPercentage,
                        rating = rating,
                        description = description
                    )
                } catch (e: Exception) {
                    println("OpenAIApiClient: Failed to parse item $index: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            println("OpenAIApiClient: Parse error: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}
