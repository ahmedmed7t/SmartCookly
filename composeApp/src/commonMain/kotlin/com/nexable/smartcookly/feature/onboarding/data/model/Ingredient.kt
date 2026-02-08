package com.nexable.smartcookly.feature.onboarding.data.model

enum class Ingredient(val displayName: String, val emoji: String) {
    NOTHING("Nothing", "✨"),
    PEANUTS("Peanuts", "🥜"),
    TREE_NUTS("Tree Nuts", "🌰"),
    MILK("Milk (Dairy)", "🥛"),
    EGGS("Eggs", "🥚"),
    FISH("Fish", "🐟"),
    SHELLFISH("Shellfish", "🦐"),
    SOY("Soy", "🫘"),
    WHEAT("Wheat (Gluten)", "🌾"),
    SESAME("Sesame", "🫓"),
    MUSTARD("Mustard", "🌿"),
    CELERY("Celery", "🥬"),
    LUPIN("Lupin", "🫒"),
    SULFITES("Sulfites", "🧪"),
    CORN("Corn", "🌽"),
    OTHER("Other", "➕")
}
