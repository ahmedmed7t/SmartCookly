package com.nexable.smartcookly.di

import com.nexable.smartcookly.feature.fridge.data.remote.OpenAIApiClient
import com.nexable.smartcookly.feature.fridge.data.repository.FridgeRepository
import com.nexable.smartcookly.feature.fridge.presentation.fridge.FridgeViewModel
import com.nexable.smartcookly.feature.fridge.presentation.review.ReviewScanViewModel
import com.nexable.smartcookly.platform.getOpenAIApiKey
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.module.dsl.*

expect val platformModule: Module

val sharedModule = module {
    // Repositories
    single { FridgeRepository() }
    
    // API Clients
    single { OpenAIApiClient(get(), getOpenAIApiKey()) }
    
    // ViewModels
    viewModel { FridgeViewModel(get()) }
    viewModel { ReviewScanViewModel(get(), get()) }
}