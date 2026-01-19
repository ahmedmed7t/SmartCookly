package com.nexable.smartcookly.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

interface EngineFactoryProvider {
    fun provide(): HttpClientEngine
}

val networkModule = module {
    single<HttpClient> {
        HttpClient(get<EngineFactoryProvider>().provide()) {
            install(ContentNegotiation) {
                json(json = Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
            }
//            install(Auth) {
//                bearer {
//                    loadTokens {  }
//                    refreshTokens {  }
//                }
//            }
        }
    }

    // Link Check Dependencies
//    factory { HttpCheckerRepo(get()) }
//    single { LinkCheckerUseCase(get(), get()) }
}