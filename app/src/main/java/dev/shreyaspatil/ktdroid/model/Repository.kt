package dev.shreyaspatil.ktdroid.model

import com.squareup.moshi.Json

data class Repositories(
    val items: List<Repository>
)

data class Repository(
    @Json(name = "full_name")
    val fullName: String,

    @Json(name = "stargazers_count")
    val stars: Int,

    @Json(name = "html_url")
    val repoUrl: String,

    val description: String?,
    val owner: Owner
)

data class Owner(
    @Json(name = "login")
    val name: String,

    @Json(name = "avatar_url")
    val avatarUrl: String


)