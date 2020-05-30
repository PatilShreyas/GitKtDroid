package dev.shreyaspatil.ktdroid.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dev.shreyaspatil.ktdroid.R
import dev.shreyaspatil.ktdroid.databinding.ActivityMainBinding
import dev.shreyaspatil.ktdroid.ui.adapter.RepositoryAdapter
import dev.shreyaspatil.ktdroid.utils.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    private val adapter by lazy {
        RepositoryAdapter(::onRepoItemClicked)
    }

    private lateinit var binding: ActivityMainBinding

    // Useful when back navigation is pressed.
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initViews()
        initData()
    }

    private fun initViews() {
        // Initialize RecyclerView
        binding.reposRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
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

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            return
        }
        showSnackbar(getString(R.string.message_back_pressed))

        backPressedTime = System.currentTimeMillis()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
    }
}
