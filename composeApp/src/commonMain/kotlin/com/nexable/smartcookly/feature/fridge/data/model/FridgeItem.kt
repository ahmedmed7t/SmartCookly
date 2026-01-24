package com.nexable.smartcookly.feature.fridge.data.model

import com.benasher44.uuid.uuid4
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class FridgeItem(
    val id: String = uuid4().toString(),
    val name: String,
    val category: FoodCategory,
    @SerialName("expiration_date")
    val expirationDate: LocalDate? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("fresh_status")
    val freshStatus: FreshStatus = FreshStatus.GOOD
) {
    fun calculateFreshStatus(): FreshStatus {
        val expirationDate = this.expirationDate ?: return FreshStatus.GOOD
        
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        val daysUntilExpiration = (expirationDate.toEpochDays() - today.toEpochDays()).toInt()
        
        return when {
            daysUntilExpiration < 0 -> FreshStatus.EXPIRED
            daysUntilExpiration <= 2 -> FreshStatus.URGENT
            daysUntilExpiration <= 5 -> FreshStatus.GOOD
            else -> FreshStatus.FRESH
        }
    }
}
