package com.nexable.smartcookly.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import com.nexable.smartcookly.di.EngineFactoryProvider

class AndroidEngineFactoryProvider : EngineFactoryProvider {
    override fun provide(): HttpClientEngine = OkHttp.create()
}