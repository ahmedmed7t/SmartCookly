package com.nexable.smartcookly.feature.subscription.presentation

import androidx.compose.runtime.Composable
import com.revenuecat.purchases.kmp.ui.revenuecatui.Paywall
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallOptions

@Composable
fun PaywallScreen(
    onDismiss: () -> Unit
) {
    val options = PaywallOptions.Builder(dismissRequest = onDismiss)
    options.shouldDisplayDismissButton = true
    val optionBuilder = options.build()

    Paywall(optionBuilder)
}
