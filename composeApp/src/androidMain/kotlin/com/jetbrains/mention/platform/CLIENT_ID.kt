package com.jetbrains.mention.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.module.Module
import org.koin.dsl.module

// TODO: IMPORTANT! Replace with your own Twitter App credentials.
private const val CLIENT_ID = "bG1zajZGSXRRVDZveUtpY1p6TkI6MTpjaQ"
private const val REDIRECT_URI = "mention://callback"
private const val AUTH_TIMEOUT_MS = 60000L // 60 seconds timeout

// A singleton object to hold the deferred result, bridging the gap between
// the suspend function and the Activity callback.
object AuthCallbackManager {
    var authCodeDeferred: CompletableDeferred<String>? = null
    
    fun clearDeferred() {
        authCodeDeferred = null
    }
}

/**
 * The 'actual' implementation of the platformModule for Android.
 * It provides the Android-specific dependencies.
 */
actual fun platformModule(): Module = module {
    single<TwitterAuthenticator> { AndroidTwitterAuthenticator(get()) }
}

/**
 * The Android implementation of the TwitterAuthenticator.
 * It uses Chrome Custom Tabs to handle the web-based login flow.
 */
class AndroidTwitterAuthenticator(private val context: Context) : TwitterAuthenticator {

    override suspend fun launchAndGetAuthCode(codeChallenge: String): String {
        val authCodeDeferred = CompletableDeferred<String>()
        AuthCallbackManager.authCodeDeferred = authCodeDeferred

        try {
            val authUrl = Uri.parse("https://twitter.com/i/oauth2/authorize").buildUpon()
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("redirect_uri", REDIRECT_URI)
                .appendQueryParameter(
                    "scope",
                    "tweet.read users.read follows.read offline.access"
                )
                .appendQueryParameter("state", "state")
                .appendQueryParameter("code_challenge", codeChallenge)
                .appendQueryParameter("code_challenge_method", "S256")
                .build()

            val customTabsIntent = CustomTabsIntent.Builder().build()
            val intent = customTabsIntent.intent
            intent.setPackage("com.android.chrome")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.applicationContext.startActivity(intent.apply { data = authUrl })

            // Wait for the AuthCallbackActivity to complete the deferred with timeout
            return withTimeoutOrNull(AUTH_TIMEOUT_MS) {
                authCodeDeferred.await()
            } ?: throw Exception("Twitter authentication timeout after ${AUTH_TIMEOUT_MS}ms")
            
        } catch (e: Exception) {
            // Clean up the deferred on any exception
            AuthCallbackManager.clearDeferred()
            throw e
        } finally {
            // Always clean up the deferred to prevent memory leaks
            AuthCallbackManager.clearDeferred()
        }
    }
}