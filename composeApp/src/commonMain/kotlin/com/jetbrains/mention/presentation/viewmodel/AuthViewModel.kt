package com.jetbrains.mention.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.mention.data.TwitterAuthRepository
import com.jetbrains.mention.data.User
import com.jetbrains.mention.platform.PKCEUtil
import com.jetbrains.mention.platform.TwitterAuthenticator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.forms.submitForm
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
private const val REDIRECT_URI =
    "mention://callback" // Must match the one in your Twitter App and AndroidManifest

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
    val accessToken: String? = null,
    val user: User? = null
)

sealed class AuthEvent {
    object LoginWithTwitter : AuthEvent()
    object Logout : AuthEvent()
}

class AuthViewModel(
    private val twitterAuthenticator: TwitterAuthenticator,
    private val httpClient: HttpClient,
    private val twitterAuthRepository: TwitterAuthRepository
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

                val codeVerifier = PKCEUtil.generateCodeVerifier()
                val codeChallenge = PKCEUtil.generateCodeChallenge(codeVerifier)

                // 2. Platforma özel web görünümünü code_challenge ile başlat
                val authCode = twitterAuthenticator.launchAndGetAuthCode(codeChallenge)

                // 3. Yetki kodunu ve DOĞRU code_verifier'ı kullanarak token al
                val tokenResponse = exchangeCodeForToken(authCode, codeVerifier)
                
                // 4. Token ile kullanıcı profilini çek
                val userResult = twitterAuthRepository.getUserProfile(tokenResponse.accessToken)
                
                if (userResult.isSuccess) {
                    val user = userResult.getOrNull()
                    println("🎉 Welcome @${user?.username} (${user?.name})!")
                    
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        accessToken = tokenResponse.accessToken,
                        user = user
                    )
                } else {
                    val error = userResult.exceptionOrNull()?.message ?: "Kullanıcı profili alınamadı"
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error
                    )
                }

            } catch (e: Exception) {
                // Handle cancellation or any other error
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Giriş işlemi başarısız oldu veya iptal edildi."
                )
            }
        }
    }

    private suspend fun exchangeCodeForToken(code: String, codeVerifier: String): TokenResponse {
        val response = httpClient.submitForm(
            url = "https://api.twitter.com/2/oauth2/token",
            formParameters = Parameters.build {
                append("code", code)
                append("grant_type", "authorization_code")
                append("redirect_uri", REDIRECT_URI)
                append("code_verifier", codeVerifier)
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