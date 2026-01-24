package com.nexable.smartcookly.feature.onboarding.data.model

enum class Disease(val displayName: String, val description: String) {
    DIABETES("Diabetes", "Blood sugar must be controlled"),
    HYPERTENSION("Hypertension (High Blood Pressure)", "Salt intake should be limited"),
    HEART_DISEASE("Heart Disease", "Low fat, low salt diet"),
    HIGH_CHOLESTEROL("High Cholesterol", "Avoid saturated & fried fats"),
    OBESITY("Obesity", "Calorie-controlled eating"),
    IBS("IBS (Irritable Bowel Syndrome)", "Sensitive to certain foods"),
    GERD("GERD (Acid Reflux)", "Avoid acidic & spicy foods"),
    CELIAC_DISEASE("Celiac Disease", "Cannot eat gluten"),
    LACTOSE_INTOLERANCE("Lactose Intolerance", "Cannot digest dairy"),
    GOUT("Gout", "Avoid high-purine foods"),
    FATTY_LIVER_DISEASE("Fatty Liver Disease", "Reduce sugar & fat"),
    CHRONIC_KIDNEY_DISEASE("Chronic Kidney Disease", "Limit salt, protein, potassium"),
    ANEMIA("Anemia", "Needs iron-rich foods"),
    PCOS("PCOS", "Low sugar, balanced carbs"),
    OTHER("Other", "Other health conditions")
}
