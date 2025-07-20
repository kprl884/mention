package com.techtactoe.mention.data

import kotlinx.serialization.Serializable

@Serializable
data class TwitterUserResponse(
    val data: User
)

@Serializable
data class TwitterErrorResponse(
    val errors: List<TwitterError>
)

@Serializable
data class TwitterError(
    val code: Int,
    val message: String
) 