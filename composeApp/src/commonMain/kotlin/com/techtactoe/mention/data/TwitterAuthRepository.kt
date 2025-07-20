package com.techtactoe.mention.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

class TwitterAuthRepository(
    private val httpClient: HttpClient
) {
    
    suspend fun getUserProfile(token: String): Result<User> {
        return try {
            val response = httpClient.get("https://api.twitter.com/2/users/me") {
                header("Authorization", "Bearer $token")
            }
            
            if (response.status.isSuccess()) {
                val userResponse: TwitterUserResponse = response.body()
                Result.success(userResponse.data)
            } else {
                when (response.status) {
                    HttpStatusCode.Unauthorized -> {
                        Result.failure(Exception("Unauthorized: Invalid or expired access token"))
                    }
                    HttpStatusCode.Forbidden -> {
                        Result.failure(Exception("Forbidden: Insufficient permissions to access user profile"))
                    }
                    HttpStatusCode.NotFound -> {
                        Result.failure(Exception("User profile not found"))
                    }
                    else -> {
                        val errorResponse: TwitterErrorResponse? = try {
                            response.body()
                        } catch (_: Exception) {
                            null
                        }
                        
                        val errorMessage = errorResponse?.errors?.firstOrNull()?.message 
                            ?: "Failed to fetch user profile: ${response.status}"
                        Result.failure(Exception(errorMessage))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
} 