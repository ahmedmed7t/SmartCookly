package com.nexable.smartcookly.di

import org.koin.dsl.module
import com.nexable.smartcookly.network.AndroidEngineFactoryProvider

actual val platformModule = module {
    single<EngineFactoryProvider> { AndroidEngineFactoryProvider() }
}
