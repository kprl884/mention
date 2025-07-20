// commonMain/kotlin/com/jetbrains/kmpapp/platform/PKCEUtil.kt
package com.jetbrains.kmpapp.platform

expect object PKCEUtil {
    fun generateCodeVerifier(): String
    fun generateCodeChallenge(codeVerifier: String): String
}