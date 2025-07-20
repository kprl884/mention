package com.jetbrains.kmpapp.platform

import kotlinx.coroutines.CompletableDeferred
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

private const val CLIENT_ID = "bG1zajZGSXRRVDZveUtpY1p6TkI6MTpjaQ"
private const val REDIRECT_URI = "mention://callback"

// A singleton object to hold the deferred result for iOS
object IOSAuthCallbackManager {
    var authCodeDeferred: CompletableDeferred<String>? = null
}

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

    override suspend fun launchAndGetAuthCode(codeChallenge: String): String {
        val authCodeDeferred = CompletableDeferred<String>()
        IOSAuthCallbackManager.authCodeDeferred = authCodeDeferred

        val authUrlString = buildString {
            append("https://twitter.com/i/oauth2/authorize")
            append("?response_type=code")
            append("&client_id=$CLIENT_ID")
            append("&redirect_uri=$REDIRECT_URI")
            append("&scope=tweet.read%20users.read%20follows.read%20offline.access")
            append("&state=state")
            append("&code_challenge=$codeChallenge")
            append("&code_challenge_method=S256")
        }

        val authUrl = NSURL.URLWithString(authUrlString)

        // Open the URL in the default browser/Safari
        UIApplication.sharedApplication.openURL(authUrl!!)

        return authCodeDeferred.await()
    }
}