package com.jetbrains.kmpapp.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.coroutines.CompletableDeferred
import org.koin.core.module.Module
import org.koin.dsl.module
import androidx.core.net.toUri

// TODO: IMPORTANT! Replace with your own Twitter App credentials.
private const val CLIENT_ID = "bG1zajZGSXRRVDZveUtpY1p6TkI6MTpjaQ\n"
private const val REDIRECT_URI = "mention://callback"

// A singleton object to hold the deferred result, bridging the gap between
// the suspend function and the Activity callback.
object AuthCallbackManager {
    var authCodeDeferred: CompletableDeferred<String>? = null
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

    override suspend fun launchAndGetAuthCode(): String {
        val authCodeDeferred = CompletableDeferred<String>()
        AuthCallbackManager.authCodeDeferred = authCodeDeferred

        val authUrl = "https://twitter.com/i/oauth2/authorize".toUri().buildUpon()
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", CLIENT_ID)
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("scope", "tweet.read users.read follows.read")
            .appendQueryParameter("state", "state")
            .appendQueryParameter("code_challenge", "challenge")
            .appendQueryParameter("code_challenge_method", "plain")
            .build()

        val customTabsIntent = CustomTabsIntent.Builder().build()
        // Use the application context and add NEW_TASK flag to launch from a non-Activity context
        val intent = customTabsIntent.intent
        intent.setPackage("com.android.chrome")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.applicationContext.startActivity(intent.apply { data = authUrl })

        // Wait for the AuthCallbackActivity to complete the deferred
        return authCodeDeferred.await()
    }
} 