package com.nexable.smartcookly

import android.app.Application
import com.google.firebase.FirebaseApp
import com.nexable.smartcookly.di.initKoin
import org.koin.android.ext.koin.androidContext

class CooklyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        initKoin {
            androidContext(this@CooklyApplication)
        }
    }
}