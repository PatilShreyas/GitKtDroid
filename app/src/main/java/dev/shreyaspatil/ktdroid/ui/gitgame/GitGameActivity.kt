package dev.shreyaspatil.ktdroid.ui.gitgame

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.api.load
import com.google.android.material.textfield.TextInputLayout
import dev.shreyaspatil.ktdroid.R
import dev.shreyaspatil.ktdroid.databinding.ActivityGitGameBinding
import dev.shreyaspatil.ktdroid.model.GameResult
import dev.shreyaspatil.ktdroid.model.User
import dev.shreyaspatil.ktdroid.utils.State
import dev.shreyaspatil.ktdroid.utils.hide
import dev.shreyaspatil.ktdroid.utils.show
import dev.shreyaspatil.ktdroid.utils.showSnackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class GitGameActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            GitGameViewModelFactory(this)
        )[GitGameViewModel::class.java]
    }

    private lateinit var binding: ActivityGitGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGitGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initData()
    }

    private fun initViews() {
        binding.buttonLoad.setOnClickListener(::loadPlayers)
        binding.buttonPlay.setOnClickListener(::playGame)
    }

    private fun initData() {
        lifecycleScope.launch {
            viewModel.playersState.collect { userState ->
                println("DATA = $userState")
                showSnackbar("$userState")
                when {
                    userState.first is State.Loading && userState.second is State.Loading -> {
                        //TODO Loading
                    }
                    userState.first is State.Success && userState.second is State.Success -> {
                        showPlayers(
                            (userState.first as State.Success<User>).data,
                            (userState.second as State.Success<User>).data
                        )

                        // TODO Stop Loading
                    }

                    userState.first is State.Error -> showFieldError(binding.fieldUsername1)
                    userState.second is State.Error -> showFieldError(binding.fieldUsername2)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.gameState.collect { gameState ->
                when (gameState) {
                    is State.Loading -> {
                        // TODO show loading
                    }
                    is State.Success -> {
                        // TODO Stop loading
                        showPlayerScores(gameState.data)
                    }
                    is State.Error -> {
                        // TODO Stop loading
                        showSnackbar(gameState.message)
                    }
                }
            }
        }
    }

    private fun showPlayerScores(result: GameResult) {
        val scoreFirst = result.score.first
        val scoreSecond = result.score.second

        val resultMessage = when {
            result.score.first == result.score.second -> {
                getString(R.string.message_result_tie)
            }
            scoreFirst > scoreSecond -> {
                getString(R.string.message_result_won, result.username.first)
            }
            else -> {
                getString(R.string.message_result_won, result.username.second)
            }
        }

        binding.run {
            profile1.run {
                userStars.text = result.totalStars.first.toString()
                userFollowers.text = result.followers.first.toString()
                userScore.text = scoreFirst.toString()
            }
            profile2.run {
                userStars.text = result.totalStars.second.toString()
                userFollowers.text = result.followers.second.toString()
                userScore.text = scoreSecond.toString()
            }

            textResult.text = resultMessage
        }
    }

    private fun showPlayers(player1: User, player2: User) {
        binding.run {
            profile1.run {
                playerUsername.text = player1.username
                avatarView.load(player1.avatarUrl)
            }
            profile2.run {
                playerUsername.text = player2.username
                avatarView.load(player2.avatarUrl)
            }
        }

        binding.buttonPlay.show()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun loadPlayers(view: View) {
        if (isValidInput()) {
            viewModel.loadUsers(
                binding.fieldUsername1.editText?.text.toString().trim(),
                binding.fieldUsername2.editText?.text.toString().trim()
            )
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun playGame(view: View) {
        viewModel.playGame()
    }

    private fun isValidInput(): Boolean {
        val username1 = binding.fieldUsername1.editText?.text.toString()
        val username2 = binding.fieldUsername2.editText?.text.toString()

        if (username1.isBlank()) {
            showFieldError(binding.fieldUsername1)
            return false
        }
        if (username2.isBlank()) {
            showFieldError(binding.fieldUsername2)
            return false
        }
        if (username1 == username2) {
            showFieldError(binding.fieldUsername2, getString(R.string.message_duplicate_id))
        }

        return true
    }

    private fun showFieldError(
        inputLayout: TextInputLayout,
        message: String = getString(R.string.message_error_input)
    ) {
        inputLayout.error = message
        binding.buttonPlay.hide()
    }
}