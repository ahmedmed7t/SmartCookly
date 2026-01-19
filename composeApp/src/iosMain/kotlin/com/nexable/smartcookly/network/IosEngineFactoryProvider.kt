package com.nexable.smartcookly.network

import com.nexable.smartcookly.di.EngineFactoryProvider
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

class IosEngineFactoryProvider : EngineFactoryProvider {
    override fun provide(): HttpClientEngine = Darwin.create()
}