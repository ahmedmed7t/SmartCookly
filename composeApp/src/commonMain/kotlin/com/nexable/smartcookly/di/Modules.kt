package com.nexable.smartcookly.di

import com.nexable.smartcookly.data.local.AppPreferences
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.auth.presentation.login.LoginViewModel
import com.nexable.smartcookly.feature.auth.presentation.signup.SignUpViewModel
import com.nexable.smartcookly.feature.fridge.data.remote.OpenAIApiClient
import com.nexable.smartcookly.feature.fridge.data.repository.FridgeRepository
import com.nexable.smartcookly.feature.fridge.data.repository.IngredientRepository
import com.nexable.smartcookly.feature.fridge.presentation.add.AddIngredientViewModel
import com.nexable.smartcookly.feature.fridge.presentation.fridge.FridgeViewModel
import com.nexable.smartcookly.feature.fridge.presentation.review.ReviewScanViewModel
import com.nexable.smartcookly.feature.onboarding.presentation.OnboardingViewModel
import com.nexable.smartcookly.feature.profile.presentation.ProfileViewModel
import com.nexable.smartcookly.feature.profile.presentation.edit.EditPreferenceViewModel
import com.nexable.smartcookly.feature.user.data.repository.UserRepository
import com.nexable.smartcookly.platform.getOpenAIApiKey
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.module.dsl.*

expect val platformModule: Module

val sharedModule = module {
    // Settings
    single { Settings() }
    single { AppPreferences(get()) }
    
    // Repositories
    single { FridgeRepository() }
    single { IngredientRepository() }
    single { AuthRepository() }
    single { UserRepository() }
    
    // API Clients
    single { OpenAIApiClient(get(), getOpenAIApiKey()) }
    
    // ViewModels
    viewModel { FridgeViewModel(get(), get(), get()) }
    viewModel { ReviewScanViewModel(get(), get()) }
    viewModel { AddIngredientViewModel(get(), get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { SignUpViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { EditPreferenceViewModel(get(), get()) }
}