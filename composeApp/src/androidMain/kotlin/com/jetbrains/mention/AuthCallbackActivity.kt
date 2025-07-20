package com.jetbrains.mention

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.jetbrains.mention.platform.AuthCallbackManager

class AuthCallbackActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val uri = intent.data
            if (uri != null && uri.toString().startsWith("mention://callback")) {
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
                } else {
                    // No code or error parameter found
                    AuthCallbackManager.authCodeDeferred?.completeExceptionally(
                        Exception("Geçersiz callback parametreleri alındı.")
                    )
                }
            } else {
                // Invalid URI
                AuthCallbackManager.authCodeDeferred?.completeExceptionally(
                    Exception("Geçersiz geri dönüş URI'si alındı: ${uri?.toString()}")
                )
            }
        } catch (e: Exception) {
            // Handle any unexpected errors
            AuthCallbackManager.authCodeDeferred?.completeExceptionally(
                Exception("Callback işlemi sırasında hata oluştu: ${e.message}")
            )
        } finally {
            // Always clean up the deferred to prevent memory leaks
            AuthCallbackManager.clearDeferred()
        }

        // Close this activity
        finish()
    }
} 