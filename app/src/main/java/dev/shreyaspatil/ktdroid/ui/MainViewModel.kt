package dev.shreyaspatil.ktdroid.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shreyaspatil.ktdroid.model.Repositories
import dev.shreyaspatil.ktdroid.repository.KtDroidGitHubRepository
import dev.shreyaspatil.ktdroid.utils.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@FlowPreview
class MainViewModel(private val repository: KtDroidGitHubRepository) : ViewModel() {
    private val _repositories = MutableLiveData<State<Repositories>>()

    val repositories: LiveData<State<Repositories>> = _repositories

    fun getData() {
        viewModelScope.launch {
            repository.getRepositories().collect {
                _repositories.value = it
            }
        }
    }
}