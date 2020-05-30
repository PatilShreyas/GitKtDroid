package dev.shreyaspatil.ktdroid.model

import com.squareup.moshi.Json

data class User(
    @Json(name = "login")
    val username: String,

    @Json(name = "avatar_url")
    val avatarUrl: String,

    @Json(name = "followers")
    val followers: Int
)