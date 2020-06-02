package dev.shreyaspatil.ktdroid.ui.ktdroid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shreyaspatil.ktdroid.model.Repositories
import dev.shreyaspatil.ktdroid.repository.KtDroidGitHubRepository
import dev.shreyaspatil.ktdroid.utils.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@FlowPreview
class KtRepositoryViewModel(private val repository: KtDroidGitHubRepository) : ViewModel() {
    private val _repositories =
        MutableStateFlow<State<Repositories>>(State.idle())

    val repositories: StateFlow<State<Repositories>> = _repositories

    fun loadRepositories() {
        viewModelScope.launch {
            repository.getRepositories().collect {
                _repositories.value = it
            }
        }
    }
}