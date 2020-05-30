package dev.shreyaspatil.ktdroid.repository

import dev.shreyaspatil.ktdroid.api.GitHubService
import dev.shreyaspatil.ktdroid.model.Repository
import dev.shreyaspatil.ktdroid.model.User
import dev.shreyaspatil.ktdroid.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

@FlowPreview
@ExperimentalCoroutinesApi
class UserRepository(private val githubService: GitHubService) {

    fun getUser(username: String): Flow<State<User>> {
        return object : NetworkBoundRepository<User>() {
            override suspend fun fetchFromRemote(): Response<User> =
                githubService.getUser(username)
        }.asFlow().flowOn(Dispatchers.IO)
    }

    fun getRepositoriesByUser(username: String): Flow<State<List<Repository>>> {
        return object : NetworkBoundRepository<List<Repository>>() {
            override suspend fun fetchFromRemote(): Response<List<Repository>> =
                githubService.getRepositoriesByUser(username)
        }.asFlow().flowOn(Dispatchers.IO)
    }
}