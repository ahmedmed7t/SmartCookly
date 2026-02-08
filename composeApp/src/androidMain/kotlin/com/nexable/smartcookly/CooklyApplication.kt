package com.nexable.smartcookly

import android.app.Application
import com.google.firebase.FirebaseApp
import com.nexable.smartcookly.di.initKoin
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import com.revenuecat.purchases.kmp.LogLevel
import org.koin.android.ext.koin.androidContext

class CooklyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        initKoin {
            androidContext(this@CooklyApplication)
        }
        
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(
                apiKey = BuildConfig.REVENUECAT_API_KEY
            ).build()
        )
    }
}