package com.jetbrains.kmpapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.jetbrains.kmpapp.platform.AuthCallbackManager

class AuthCallbackActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.data
        if (uri != null && uri.toString().startsWith("kmpapp://callback")) {
            val code = uri.getQueryParameter("code")
            val error = uri.getQueryParameter("error")

            if (code != null) {
                // Success: complete the deferred with the auth code
                AuthCallbackManager.authCodeDeferred?.complete(code)
            } else if (error != null) {
                // Error or cancellation: complete with an exception
                AuthCallbackManager.authCodeDeferred?.completeExceptionally(
                    Exception("Giriş iptal edildi: $error")
                )
            }
        } else {
             AuthCallbackManager.authCodeDeferred?.completeExceptionally(
                Exception("Geçersiz geri dönüş URI'si alındı.")
             )
        }

        // Close this activity
        finish()
    }
} 