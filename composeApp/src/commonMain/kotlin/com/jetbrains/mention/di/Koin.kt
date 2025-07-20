package com.jetbrains.mention.di

import com.jetbrains.mention.data.InMemoryMuseumStorage
import com.jetbrains.mention.data.KtorMuseumApi
import com.jetbrains.mention.data.MuseumApi
import com.jetbrains.mention.data.MuseumRepository
import com.jetbrains.mention.data.MuseumStorage
import com.jetbrains.mention.data.TwitterAuthRepository
import com.jetbrains.mention.platform.platformModule
import com.jetbrains.mention.presentation.viewmodel.AuthViewModel
import com.jetbrains.mention.screens.detail.DetailViewModel
import com.jetbrains.mention.screens.list.ListViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val dataModule = module {
    single {
        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
        HttpClient {
            install(ContentNegotiation) {
                // TODO Fix API so it serves application/json
                json(json, contentType = ContentType.Any)
            }
            // This block will now compile correctly
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    single<MuseumApi> { KtorMuseumApi(get()) }
    single<MuseumStorage> { InMemoryMuseumStorage() }
    single {
        MuseumRepository(get(), get()).apply {
            initialize()
        }
    }
    
    // Twitter Auth Repository
    single { TwitterAuthRepository(get()) }
}

val viewModelModule = module {
    // AuthViewModel now requires dependencies, so we define its creation manually
    factory { AuthViewModel(get(), get(), get()) }
    factoryOf(::ListViewModel)
    factoryOf(::DetailViewModel)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            dataModule,
            viewModelModule,
            platformModule() // This will now resolve correctly
        )
    }
}