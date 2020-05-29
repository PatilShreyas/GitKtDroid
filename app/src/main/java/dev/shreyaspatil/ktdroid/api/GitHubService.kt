package dev.shreyaspatil.ktdroid.api

import dev.shreyaspatil.ktdroid.model.Repositories
import retrofit2.Response
import retrofit2.http.GET

interface GitHubService {

    @GET("/search/repositories?q=android+language:kotlin&sort=stars&order=desc")
    suspend fun getRepositories(): Response<Repositories>

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}