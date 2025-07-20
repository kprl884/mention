package com.techtactoe.mention.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class TwitterAuthRepository(
    private val httpClient: HttpClient
) {
    
    suspend fun getUserProfile(token: String): Result<User> {
        return try {
            println("🔍 Fetching user profile with token: ${token.take(10)}...")
            
            val response = httpClient.get("https://api.twitter.com/2/users/me") {
                header("Authorization", "Bearer $token")
            }
            
            println("📡 Response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                try {
                    // First try: Direct serialization
                    println("🔄 Attempting direct serialization...")
                    val userResponse: TwitterUserResponse = response.body()
                    println("✅ Direct serialization successful: ${userResponse.data}")
                    Result.success(userResponse.data)
                } catch (e: Exception) {
                    println("❌ Direct serialization failed: ${e.message}")
                    println("📄 Raw response: ${response.body<String>()}")
                    
                    // Second try: Manual JSON parsing for more flexible handling
                    try {
                        println("🔄 Attempting manual JSON parsing...")
                        val jsonResponse: JsonElement = response.body()
                        val data = jsonResponse.jsonObject["data"]?.jsonObject
                        
                        if (data != null) {
                            val user = User(
                                id = data["id"]?.jsonPrimitive?.content ?: "",
                                name = data["name"]?.jsonPrimitive?.content ?: "",
                                username = data["username"]?.jsonPrimitive?.content ?: "",
                                profileImageUrl = data["profile_image_url"]?.jsonPrimitive?.content
                            )
                            println("✅ Manual parsing successful: User = $user")
                            Result.success(user)
                        } else {
                            println("❌ No data field found in response")
                            Result.failure(Exception("Invalid response format: missing data field"))
                        }
                    } catch (manualException: Exception) {
                        println("❌ Manual parsing also failed: ${manualException.message}")
                        Result.failure(Exception("Failed to parse user data: ${manualException.message}"))
                    }
                }
            } else {
                when (response.status) {
                    HttpStatusCode.Unauthorized -> {
                        println("❌ Unauthorized: Invalid or expired access token")
                        Result.failure(Exception("Unauthorized: Invalid or expired access token"))
                    }
                    HttpStatusCode.Forbidden -> {
                        println("❌ Forbidden: Insufficient permissions")
                        Result.failure(Exception("Forbidden: Insufficient permissions to access user profile"))
                    }
                    HttpStatusCode.NotFound -> {
                        println("❌ User profile not found")
                        Result.failure(Exception("User profile not found"))
                    }
                    else -> {
                        val errorResponse: TwitterErrorResponse? = try {
                            response.body()
                        } catch (e: Exception) {
                            null
                        }
                        
                        val errorMessage = errorResponse?.errors?.firstOrNull()?.message 
                            ?: "Failed to fetch user profile: ${response.status}"
                        println("❌ API Error: $errorMessage")
                        Result.failure(Exception(errorMessage))
                    }
                }
            }
        } catch (e: Exception) {
            println("❌ Network error: ${e.message}")
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
} 