package com.nexable.smartcookly.di

import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    // Repositories
//    single { ProductRepository(get()) }
//
//    // ViewModels
//    viewModel { ProductViewModel(get()) }
}