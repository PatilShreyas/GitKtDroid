package dev.shreyaspatil.ktdroid.api

import dev.shreyaspatil.ktdroid.model.Repositories
import dev.shreyaspatil.ktdroid.model.Repository
import dev.shreyaspatil.ktdroid.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {

    @GET("/search/repositories?q=android+language:kotlin&sort=stars&order=desc&per_page=100")
    suspend fun getKtAndroidRepositories(): Response<Repositories>

    @GET("/users/{user}")
    suspend fun getUser(@Path("user") username: String): Response<User>

    @GET("/users/{user}/repos?per_page=10000000")
    suspend fun getRepositoriesByUser(@Path("user") username: String): Response<List<Repository>>

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}