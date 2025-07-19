package com.jetbrains.kmpapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.platform.TwitterAuthenticator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.Parameters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: IMPORTANT! Replace with your own Twitter App credentials.
// It is strongly recommended to store these securely (e.g., in local.properties)
// and not to commit them to version control.
private const val CLIENT_ID = "bG1zajZGSXRRVDZveUtpY1p6TkI6MTpjaQ"
private const val CLIENT_SECRET = "WGlmHwE-Je0cxgtiOjeoFB6pb3yBn9EdWGI5qK5ptwDdhm68nh"
private const val REDIRECT_URI = "mention://callback" // Must match the one in your Twitter App and AndroidManifest

@Serializable
data class TokenResponse(
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("access_token") val accessToken: String,
    @SerialName("scope") val scope: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
)

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val accessToken: String? = null
)

sealed class AuthEvent {
    object LoginWithTwitter : AuthEvent()
    object Logout : AuthEvent()
}

class AuthViewModel(
    private val twitterAuthenticator: TwitterAuthenticator,
    private val httpClient: HttpClient
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun handleEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.LoginWithTwitter -> {
                initiateTwitterLogin()
            }
            is AuthEvent.Logout -> {
                logout()
            }
        }
    }

    private fun initiateTwitterLogin() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)

                // 1. Launch platform-specific web view to get the authorization code
                val authCode = twitterAuthenticator.launchAndGetAuthCode()

                // 2. Exchange the authorization code for an access token
                val tokenResponse = exchangeCodeForToken(authCode)

                // 3. Login successful
                _state.value = _state.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    accessToken = tokenResponse.accessToken
                )

            } catch (e: Exception) {
                // Handle cancellation or any other error
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Giriş işlemi başarısız oldu veya iptal edildi."
                )
            }
        }
    }

    private suspend fun exchangeCodeForToken(code: String): TokenResponse {
        val response = httpClient.submitForm(
            url = "https://api.twitter.com/2/oauth2/token",
            formParameters = Parameters.build {
                append("code", code)
                append("grant_type", "authorization_code")
                append("redirect_uri", REDIRECT_URI)
                append("code_verifier", "challenge") // For PKCE, a unique verifier should be generated and stored for each request
            }
        ) {
            basicAuth(CLIENT_ID, CLIENT_SECRET)
        }
        return response.body()
    }

    private fun logout() {
        _state.value = AuthState()
    }
}