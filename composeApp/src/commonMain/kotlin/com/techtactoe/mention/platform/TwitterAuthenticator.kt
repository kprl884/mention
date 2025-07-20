// In: src/commonMain/kotlin/com/techtactoe/mention/platform/TwitterAuthenticator.kt
package com.techtactoe.mention.platform

import org.koin.core.module.Module

/**
 * An interface for handling the platform-specific Twitter authentication flow.
 */
interface TwitterAuthenticator {
    /**
     * Launches the Twitter authentication process.
     * @param codeChallenge The S256-hashed code challenge generated for this login attempt.
     * @return The authorization code from Twitter if successful.
     * @throws Exception if the user cancels or an error occurs.
     */
    suspend fun launchAndGetAuthCode(codeChallenge: String): String
}

/**
 * Provides a Koin module with platform-specific dependencies, like the TwitterAuthenticator.
 */
expect fun platformModule(): Module