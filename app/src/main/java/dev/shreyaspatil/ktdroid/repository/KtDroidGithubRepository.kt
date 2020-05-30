package dev.shreyaspatil.ktdroid.repository

import dev.shreyaspatil.ktdroid.api.GitHubService
import dev.shreyaspatil.ktdroid.model.Repositories
import dev.shreyaspatil.ktdroid.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

@FlowPreview
@ExperimentalCoroutinesApi
class KtDroidGitHubRepository(private val githubService: GitHubService) {

    fun getRepositories(): Flow<State<Repositories>> {
        return object : NetworkBoundRepository<Repositories>() {
            override suspend fun fetchFromRemote(): Response<Repositories> =
                githubService.getKtAndroidRepositories()
        }.asFlow().flowOn(Dispatchers.IO)
    }
}