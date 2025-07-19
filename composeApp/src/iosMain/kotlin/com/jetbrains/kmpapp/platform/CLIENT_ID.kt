package com.jetbrains.kmpapp.platform

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * The 'actual' implementation of the platformModule for iOS.
 * It provides the iOS-specific dependencies.
 */
actual fun platformModule(): Module = module {
    single<TwitterAuthenticator> { IOSTwitterAuthenticator() }
}

/**
 * The iOS implementation of the TwitterAuthenticator.
 * TODO: Implement iOS-specific Twitter authentication
 */
class IOSTwitterAuthenticator : TwitterAuthenticator {

    override suspend fun launchAndGetAuthCode(): String {
        // TODO: Implement iOS-specific Twitter authentication
        throw NotImplementedError("iOS Twitter authentication not yet implemented")
    }
} 