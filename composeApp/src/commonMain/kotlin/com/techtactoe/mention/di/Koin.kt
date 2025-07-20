package com.techtactoe.mention.di

import com.techtactoe.mention.data.TwitterAuthRepository
import com.techtactoe.mention.platform.platformModule
import com.techtactoe.mention.presentation.viewmodel.AuthViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val dataModule = module {
    single {
        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
            coerceInputValues = true
            useArrayPolymorphism = false
            classDiscriminator = "type"
            encodeDefaults = false
        }
        HttpClient {
            install(ContentNegotiation) {
                json(json, contentType = ContentType.Any)
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    // Twitter Auth Repository
    single { TwitterAuthRepository(get()) }
}

val viewModelModule = module {
    // AuthViewModel
    factory { AuthViewModel(get(), get(), get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            dataModule,
            viewModelModule,
            platformModule()
        )
    }
}