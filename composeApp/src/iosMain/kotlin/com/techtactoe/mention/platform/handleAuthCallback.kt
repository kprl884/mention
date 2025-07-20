package com.techtactoe.mention.platform

import kotlinx.coroutines.CompletableDeferred

// A singleton object to hold the deferred result for iOS
object IOSAuthCallbackManager {
    var authCodeDeferred: CompletableDeferred<String>? = null
    
    fun clearDeferred() {
        authCodeDeferred = null
    }
}

/**
 * This function should be called from your iOS app's URL scheme handler
 * to complete the authentication flow.
 */
fun handleAuthCallback(url: String) {
    try {
        // Parse the authorization code from the URL
        val uri = url.substringAfter("mention://callback")
        if (uri.contains("code=")) {
            val code = uri.substringAfter("code=").substringBefore("&")
            IOSAuthCallbackManager.authCodeDeferred?.complete(code)
        } else if (uri.contains("error=")) {
            val error = uri.substringAfter("error=").substringBefore("&")
            IOSAuthCallbackManager.authCodeDeferred?.completeExceptionally(
                Exception("Authorization failed: $error")
            )
        } else {
            IOSAuthCallbackManager.authCodeDeferred?.completeExceptionally(
                Exception("Authorization failed or was cancelled")
            )
        }
    } catch (e: Exception) {
        IOSAuthCallbackManager.authCodeDeferred?.completeExceptionally(
            Exception("Error parsing callback URL: ${e.message}")
        )
    } finally {
        IOSAuthCallbackManager.clearDeferred()
    }
}