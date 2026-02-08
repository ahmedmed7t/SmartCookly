package com.nexable.smartcookly.feature.onboarding.data.model

enum class Disease(val displayName: String, val description: String, val emoji: String) {
    NOTHING("Nothing", "No health conditions", "✨"),
    DIABETES("Diabetes", "Blood sugar must be controlled", "💉"),
    HYPERTENSION("Hypertension", "Salt intake should be limited", "❤️‍🩹"),
    HEART_DISEASE("Heart Disease", "Low fat, low salt diet", "🫀"),
    HIGH_CHOLESTEROL("High Cholesterol", "Avoid saturated & fried fats", "🩺"),
    OBESITY("Obesity", "Calorie-controlled eating", "⚖️"),
    IBS("IBS", "Sensitive to certain foods", "🌡️"),
    GERD("GERD (Acid Reflux)", "Avoid acidic & spicy foods", "🔥"),
    CELIAC_DISEASE("Celiac Disease", "Cannot eat gluten", "🌾"),
    LACTOSE_INTOLERANCE("Lactose Intolerance", "Cannot digest dairy", "🥛"),
    GOUT("Gout", "Avoid high-purine foods", "🦶"),
    FATTY_LIVER_DISEASE("Fatty Liver", "Reduce sugar & fat", "🫁"),
    CHRONIC_KIDNEY_DISEASE("Kidney Disease", "Limit salt, protein, potassium", "🫘"),
    ANEMIA("Anemia", "Needs iron-rich foods", "🩸"),
    PCOS("PCOS", "Low sugar, balanced carbs", "♀️"),
    OTHER("Other", "Other health conditions", "➕")
}
