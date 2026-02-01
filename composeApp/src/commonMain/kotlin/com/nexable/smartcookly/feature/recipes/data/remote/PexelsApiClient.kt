package com.nexable.smartcookly.feature.recipes.data.remote

import com.nexable.smartcookly.feature.recipes.data.remote.dto.PexelsSearchResponse
import com.nexable.smartcookly.netwrokUtils.BaseNetworkClient
import com.nexable.smartcookly.netwrokUtils.NetworkError
import com.nexable.smartcookly.netwrokUtils.Result
import com.nexable.smartcookly.netwrokUtils.map
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PexelsApiClient(
    httpClient: HttpClient,
    private val apiKey: String
) : BaseNetworkClient(httpClient) {

    companion object {
        private const val BASE_URL = "https://api.pexels.com/v1"
        private const val SEARCH_ENDPOINT = "$BASE_URL/search"
    }

    /**
     * Search for a food image based on the recipe/dish name.
     * Returns the medium-sized image URL for optimal loading performance.
     */
    suspend fun searchFoodImage(query: String): Result<String, NetworkError> {
        // Clean up the query and add "food" to improve results
        val searchQuery = "${query.trim()} food dish"
        
        println("PexelsApiClient: Searching for image: $searchQuery")
        
        return get<PexelsSearchResponse>(SEARCH_ENDPOINT) {
            header("Authorization", apiKey)
            contentType(ContentType.Application.Json)
            parameter("query", searchQuery)
            parameter("per_page", 1)
            parameter("orientation", "landscape")
        }.map { response ->
            val imageUrl = response.photos.firstOrNull()?.src?.medium ?: ""
            println("PexelsApiClient: Found image URL: $imageUrl")
            imageUrl
        }
    }
    
    /**
     * Search for multiple food images in batch.
     * Returns a map of recipe names to image URLs.
     */
    suspend fun searchFoodImages(queries: List<String>): Map<String, String> {
        val results = mutableMapOf<String, String>()
        
        for (query in queries) {
            when (val result = searchFoodImage(query)) {
                is Result.Success -> {
                    if (result.data.isNotEmpty()) {
                        results[query] = result.data
                    }
                }
                is Result.Error -> {
                    println("PexelsApiClient: Failed to fetch image for '$query': ${result.error}")
                }
            }
        }
        
        return results
    }
}
