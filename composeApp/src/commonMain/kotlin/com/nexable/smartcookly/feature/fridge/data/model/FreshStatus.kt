package com.nexable.smartcookly.feature.fridge.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class FreshStatus {
    FRESH,
    GOOD,
    URGENT,
    EXPIRED
}
