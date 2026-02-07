package com.nexable.smartcookly.feature.subscription.presentation

import androidx.compose.runtime.Composable
import com.revenuecat.purchases.kmp.ui.revenuecatui.CustomerCenter

@Composable
fun CustomerCenterScreen(onDismiss: () -> Unit) {
    CustomerCenter(onDismiss = onDismiss)
}
