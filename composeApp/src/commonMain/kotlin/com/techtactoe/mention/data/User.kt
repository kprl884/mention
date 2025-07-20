package com.techtactoe.mention.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val username: String,
    @SerialName("profile_image_url")
    val profileImageUrl: String? = null
)