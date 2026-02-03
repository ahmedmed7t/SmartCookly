package com.nexable.smartcookly.di

import com.nexable.smartcookly.data.local.AppPreferences
import com.nexable.smartcookly.feature.auth.data.repository.AuthRepository
import com.nexable.smartcookly.feature.auth.presentation.login.LoginViewModel
import com.nexable.smartcookly.feature.auth.presentation.signup.SignUpViewModel
import com.nexable.smartcookly.feature.fridge.data.remote.OpenAIApiClient
import com.nexable.smartcookly.feature.favorites.data.repository.FavoritesRepository
import com.nexable.smartcookly.feature.favorites.presentation.FavoritesViewModel
import com.nexable.smartcookly.feature.shopping.data.repository.ShoppingRepository
import com.nexable.smartcookly.feature.shopping.presentation.ShoppingViewModel
import com.nexable.smartcookly.feature.fridge.data.repository.FridgeRepository
import com.nexable.smartcookly.feature.fridge.data.repository.IngredientRepository
import com.nexable.smartcookly.feature.fridge.data.repository.ImageStorageRepository
import com.nexable.smartcookly.feature.fridge.presentation.add.AddIngredientViewModel
import com.nexable.smartcookly.feature.fridge.presentation.fridge.FridgeViewModel
import com.nexable.smartcookly.feature.fridge.presentation.review.ReviewScanViewModel
import com.nexable.smartcookly.feature.onboarding.presentation.OnboardingViewModel
import com.nexable.smartcookly.feature.profile.presentation.ProfileViewModel
import com.nexable.smartcookly.feature.profile.presentation.edit.EditPreferenceViewModel
import com.nexable.smartcookly.feature.recipes.data.repository.RecipeRepository
import com.nexable.smartcookly.feature.recipes.presentation.RecipesViewModel
import com.nexable.smartcookly.feature.recipes.presentation.discover.DiscoverRecipesViewModel
import com.nexable.smartcookly.feature.recipes.presentation.cooking.CookingModeViewModel
import com.nexable.smartcookly.feature.user.data.repository.UserRepository
import com.nexable.smartcookly.platform.ImageUploader
import com.nexable.smartcookly.platform.ImageUploaderImpl
import com.nexable.smartcookly.platform.getOpenAIApiKey
import com.nexable.smartcookly.platform.getPexelsApiKey
import com.nexable.smartcookly.feature.recipes.data.remote.PexelsApiClient
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.module.dsl.*

expect val platformModule: Module

val sharedModule = module {
    // Settings
    single { Settings() }
    single { AppPreferences(get()) }
    
    // Platform Services
    single<ImageUploader> { ImageUploaderImpl() }
    
    // Repositories
    single { FridgeRepository() }
    single { IngredientRepository() }
    single { ImageStorageRepository(get()) }
    single { AuthRepository() }
    single { UserRepository() }
    single { RecipeRepository(get(), get()) }
    single { FavoritesRepository() }
    single { ShoppingRepository() }
    
    // API Clients
    single { OpenAIApiClient(get(), getOpenAIApiKey()) }
    single { PexelsApiClient(get(), getPexelsApiKey()) }
    
    // ViewModels
    viewModel { FridgeViewModel(get(), get(), get()) }
    viewModel { (imageBytes: ByteArray) ->
        ReviewScanViewModel(imageBytes, get(), get(), get(), get(), get())
    }
    viewModel { AddIngredientViewModel(get(), get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { SignUpViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { EditPreferenceViewModel(get(), get()) }
    viewModel { RecipesViewModel(get(), get()) }
    viewModel { DiscoverRecipesViewModel(get(), get(), get(), get(), get()) }
    viewModel { CookingModeViewModel(get(), get(), get()) }
    viewModel { FavoritesViewModel(get(), get()) }
    viewModel { ShoppingViewModel(get(), get()) }
}