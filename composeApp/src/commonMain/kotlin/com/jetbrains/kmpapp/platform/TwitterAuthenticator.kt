
package com.jetbrains.kmpapp.platform

import org.koin.core.module.Module

/**
 * An interface for handling the platform-specific Twitter authentication flow.
 */
interface TwitterAuthenticator {
    /**
     * Launches the Twitter authentication process.
     * @return The authorization code from Twitter if successful.
     * @throws Exception if the user cancels or an error occurs.
     */
    suspend fun launchAndGetAuthCode(): String
}

/**
 * Provides a Koin module with platform-specific dependencies, like the TwitterAuthenticator.
 */
expect fun platformModule(): Module