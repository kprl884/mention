// commonMain/kotlin/com/techtactoe/mention/platform/PKCEUtil.kt
package com.techtactoe.mention.platform

expect object PKCEUtil {
    fun generateCodeVerifier(): String
    fun generateCodeChallenge(codeVerifier: String): String
}