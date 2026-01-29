package com.nexable.smartcookly.feature.fridge.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageAnalysisRequest(
    val model: String = "gpt-4o",
    val messages: List<Message>,
    @SerialName("max_completion_tokens")
    val max_completion_tokens: Int = 1000
)

@Serializable
data class Message(
    val role: String,
    val content: List<Content>
)

@Serializable
data class Content(
    val type: String,
    val text: String? = null,
    @SerialName("image_url")
    val imageUrl: ImageUrl? = null
)

@Serializable
data class ImageUrl(
    val url: String
)
