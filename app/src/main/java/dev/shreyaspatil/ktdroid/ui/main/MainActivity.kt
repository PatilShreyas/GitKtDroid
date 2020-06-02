package dev.shreyaspatil.ktdroid.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dev.shreyaspatil.ktdroid.R
import dev.shreyaspatil.ktdroid.databinding.ActivityMainBinding
import dev.shreyaspatil.ktdroid.ui.gitgame.GitGameActivity
import dev.shreyaspatil.ktdroid.ui.ktdroid.KtRepositoryActivity
import dev.shreyaspatil.ktdroid.utils.showSnackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@FlowPreview
@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Useful when back navigation is pressed.
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        binding.cardPlayGame.setOnClickListener(this::onClick)
        binding.cardRepo.setOnClickListener(this::onClick)
    }

    private fun onClick(view: View) {
        val activityClass = when (view) {
            binding.cardPlayGame -> GitGameActivity::class.java
            binding.cardRepo -> KtRepositoryActivity::class.java
            else -> null
        }

        activityClass?.let {
            startActivity(Intent(this@MainActivity, it))
        }
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            return
        }
        showSnackbar(getString(R.string.message_back_pressed))

        backPressedTime = System.currentTimeMillis()
    }
}