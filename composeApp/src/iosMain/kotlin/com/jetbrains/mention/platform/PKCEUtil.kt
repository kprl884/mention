package com.jetbrains.mention.platform

actual object PKCEUtil {
    actual fun generateCodeVerifier(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"
        return (1..32).map { chars.random() }.joinToString("")
    }

    actual fun generateCodeChallenge(codeVerifier: String): String {
        // Basit bir hash implementasyonu - gerçek uygulamada SHA-256 kullanılmalı
        return codeVerifier.hashCode().toString()
    }
} 