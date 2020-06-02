package dev.shreyaspatil.ktdroid.ui.ktdroid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.shreyaspatil.ktdroid.R
import dev.shreyaspatil.ktdroid.databinding.ActivityRepositoryListBinding
import dev.shreyaspatil.ktdroid.ui.ktdroid.adapter.RepositoryAdapter
import dev.shreyaspatil.ktdroid.utils.State
import dev.shreyaspatil.ktdroid.utils.showSnackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class KtRepositoryActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            KtRepositoryViewModelFactory(this)
        )[KtRepositoryViewModel::class.java]
    }

    private val adapter by lazy {
        RepositoryAdapter(::onRepoItemClicked)
    }

    private lateinit var binding: ActivityRepositoryListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepositoryListBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initViews()
        initData()
    }

    private fun initViews() {
        title = getString(R.string.title_ktrepo_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize RecyclerView
        binding.reposRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@KtRepositoryActivity)
            adapter = this@KtRepositoryActivity.adapter
        }

        // Initialize SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadRepositories()
        }
    }

    private fun initData() {
        lifecycleScope.launch {
            viewModel.repositories.collect { state ->
                when (state) {
                    is State.Loading -> {
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                    is State.Success -> {
                        adapter.submitList(state.data.items)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    is State.Error -> {
                        showSnackbar(getString(R.string.message_error_loading))
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }

        viewModel.loadRepositories()
    }

    private fun onRepoItemClicked(repoUrl: String) {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = repoUrl.toUri()
        })
    }
}
