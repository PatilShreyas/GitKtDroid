package dev.shreyaspatil.ktdroid.ui.gitgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shreyaspatil.ktdroid.model.GameResult
import dev.shreyaspatil.ktdroid.model.Repository
import dev.shreyaspatil.ktdroid.model.User
import dev.shreyaspatil.ktdroid.repository.UserRepository
import dev.shreyaspatil.ktdroid.utils.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

typealias UserStatePair = Pair<State<User>, State<User>>

@ExperimentalCoroutinesApi
@FlowPreview
class GitGameViewModel(private val repository: UserRepository) : ViewModel() {
    private val _playersState =
        MutableStateFlow<UserStatePair>(Pair(State.idle(), State.idle()))

    private val _gameState =
        MutableStateFlow<State<GameResult>>(State.idle())

    val playersState: StateFlow<UserStatePair> = _playersState
    val gameState: StateFlow<State<GameResult>> = _gameState

    fun loadUsers(username1: String, username2: String) {
        viewModelScope.launch {
            repository.getUser(username1)
                .zip(repository.getUser(username2)) { user1, user2 ->
                    Pair(user1, user2)
                }.collect { pair ->
                    _playersState.value = pair
                }
        }
    }

    fun playGame() {
        if (playersState.value.first !is State.Success && playersState.value.second !is State.Success) {
            _gameState.value = State.error("Players aren't loaded yet")
            return
        }
        val user1 = (playersState.value.first as State.Success<User>).data
        val user2 = (playersState.value.second as State.Success<User>).data
        viewModelScope.launch {
            repository.getRepositoriesByUser(user1.username)
                .zip(repository.getRepositoriesByUser(user2.username)) { repo1, repo2 ->
                    Pair(repo1, repo2)
                }.collect { pair ->
                    when {
                        pair.first is State.Loading && pair.second is State.Loading -> {
                            _gameState.value = State.loading()
                        }
                        pair.first is State.Error || pair.second is State.Error -> {
                            _gameState.value = State.error("Error Occurred")
                        }
                        pair.first is State.Success && pair.second is State.Success -> {
                            val repos1 = (pair.first as State.Success<List<Repository>>).data
                            val repos2 = (pair.second as State.Success<List<Repository>>).data

                            val starCount1 = repos1.sumBy { it.stars }
                            val starCount2 = repos2.sumBy { it.stars }

                            _gameState.value = State.success(
                                GameResult(
                                    username = Pair(user1.username, user2.username),
                                    totalRepos = Pair(repos1.size, repos2.size),
                                    followers = Pair(user1.followers, user2.followers),
                                    totalStars = Pair(starCount1, starCount2)
                                )
                            )
                        }
                    } // End when
                } // End collect
        } // End ViewModelScope
    }
}