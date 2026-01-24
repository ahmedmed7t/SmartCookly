package com.nexable.smartcookly.feature.fridge.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ImageAnalysisResponse(
    val id: String,
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: ResponseMessage
)

@Serializable
data class ResponseMessage(
    val role: String,
    val content: String
)
