// In: src/commonMain/kotlin/com/techtactoe/mention/platform/TwitterAuthenticator.kt
package com.techtactoe.mention.platform

import org.koin.core.module.Module

/**
 * The 'expect' interface defines the contract that all platform authenticators must follow.
 * The common code will interact with this interface.
 */
interface TwitterAuthenticator {
    /**
     * Launches the platform-specific web-based authentication flow.
     * @param codeChallenge The S256-hashed code challenge generated for this login attempt.
     * @return The authorization code from Twitter if the user approves.
     * @throws Exception if the user cancels or an error occurs.
     */
    // The 'actual' keyword has been removed from here.
    suspend fun launchAndGetAuthCode(codeChallenge: String): String
}

/**
 * The 'expect' function defines the contract for providing a platform-specific Koin module.
 * This allows each platform (Android, iOS) to provide its own dependencies.
 */
expect fun platformModule(): Module