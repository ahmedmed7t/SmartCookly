package com.nexable.smartcookly.feature.onboarding.data.model

import org.jetbrains.compose.resources.DrawableResource
import smartcookly.composeapp.generated.resources.Res
import smartcookly.composeapp.generated.resources.ic_beginner
import smartcookly.composeapp.generated.resources.ic_exppert
import smartcookly.composeapp.generated.resources.ic_intermediate

enum class CookingLevel(val displayName: String, val description: String, val icon: DrawableResource) {
    BEGINNER("Beginner", "I'm still learning the basics.", Res.drawable.ic_beginner),
    INTERMEDIATE("Intermediate", "I'm comfortable with most recipes.", Res.drawable.ic_intermediate),
    ADVANCED("Advanced", "I can handle complex techniques and recipes.", Res.drawable.ic_exppert)
}
