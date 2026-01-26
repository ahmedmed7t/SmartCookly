package com.nexable.smartcookly.di

import org.koin.core.module.Module
import org.koin.dsl.module
import com.nexable.smartcookly.network.IosEngineFactoryProvider

actual val platformModule: Module = module {
    single<EngineFactoryProvider> { IosEngineFactoryProvider() }
}
