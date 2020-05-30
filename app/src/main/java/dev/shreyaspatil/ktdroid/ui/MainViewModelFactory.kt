package dev.shreyaspatil.ktdroid.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.shreyaspatil.ktdroid.api.getGitHubService
import dev.shreyaspatil.ktdroid.repository.KtDroidGitHubRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@FlowPreview
@InternalCoroutinesApi
class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(KtDroidGitHubRepository::class.java).newInstance(
            KtDroidGitHubRepository(getGitHubService(context))
        )
    }
}