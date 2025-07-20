package com.jetbrains.kmpapp.platform

/**
 * This function should be called from your iOS app's URL scheme handler
 * to complete the authentication flow.
 */
fun handleAuthCallback(url: String) {
    // Parse the authorization code from the URL
    val uri = url.substringAfter("mention://callback")
    if (uri.contains("code=")) {
        val code = uri.substringAfter("code=").substringBefore("&")
        IOSAuthCallbackManager.authCodeDeferred?.complete(code)
    } else {
        IOSAuthCallbackManager.authCodeDeferred?.completeExceptionally(
            Exception("Authorization failed or was cancelled")
        )
    }
    IOSAuthCallbackManager.authCodeDeferred = null
}