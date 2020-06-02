package dev.shreyaspatil.ktdroid.ui.gitgame

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.api.load
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.textfield.TextInputLayout
import dev.shreyaspatil.ktdroid.R
import dev.shreyaspatil.ktdroid.databinding.ActivityGitGameBinding
import dev.shreyaspatil.ktdroid.model.GameResult
import dev.shreyaspatil.ktdroid.model.User
import dev.shreyaspatil.ktdroid.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.math.RoundingMode

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
        title = getString(R.string.title_game_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.buttonLoad.setOnClickListener(::loadPlayers)
        binding.buttonPlay.setOnClickListener(::playGame)
        binding.player1.animationView.setOnAnimationEnd(::hideAnimationView)
        binding.player2.animationView.setOnAnimationEnd(::hideAnimationView)
    }

    private fun initData() {
        lifecycleScope.launch {
            viewModel.playersState.collect { userState ->
                when {
                    userState.first is State.Success && userState.second is State.Success -> {
                        showPlayers(
                            (userState.first as State.Success<User>).data,
                            (userState.second as State.Success<User>).data
                        )
                        binding.warAnimationView.show()
                    }

                    userState.first is State.Error -> showSnackbar((userState.first as State.Error<User>).message)
                    userState.second is State.Error -> showSnackbar((userState.second as State.Error<User>).message)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.gameState.collect { gameState ->
                when (gameState) {
                    is State.Loading -> {
                        playAnimation(binding.warAnimationView)
                    }
                    is State.Success -> {
                        hideAnimationView(binding.warAnimationView)
                        showPlayerScores(gameState.data)
                    }
                    is State.Error -> {
                        hideAnimationView(binding.warAnimationView)
                        showSnackbar(gameState.message)
                    }
                }
            }
        }
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

    private fun showPlayers(user1: User, user2: User) {
        binding.run {
            player1.run {
                playerUsername.text = user1.username
                avatarView.load(user1.avatarUrl)
                userStars.text = "0"
                userFollowers.text = "0"
                userScore.text = "0"
            }
            player2.run {
                playerUsername.text = user2.username
                avatarView.load(user2.avatarUrl)
                userStars.text = "0"
                userFollowers.text = "0"
                userScore.text = "0"
            }
        }
        binding.textResult.text = getString(R.string.message_click_play)
        binding.buttonPlay.enable()
    }

    @SuppressLint("SetTextI18n")
    private fun showPlayerScores(result: GameResult) {
        val scoreFirst = result.score.first.toBigDecimal().setScale(1, RoundingMode.UP)
        val scoreSecond = result.score.second.toBigDecimal().setScale(1, RoundingMode.UP)

        val resultMessage: String
        when {
            scoreFirst == scoreSecond -> {
                resultMessage = getString(R.string.message_result_tie)
            }
            scoreFirst > scoreSecond -> {
                resultMessage = getString(R.string.message_result_won, result.username.first)
                playAnimation(binding.player1.animationView)
            }
            else -> {
                resultMessage = getString(R.string.message_result_won, result.username.second)
                playAnimation(binding.player2.animationView)
            }
        }

        binding.run {
            player1.run {
                userStars.text = result.totalStars.first.toString()
                userFollowers.text = result.followers.first.toString()
                userScore.text = "$scoreFirst"
            }
            player2.run {
                userStars.text = result.totalStars.second.toString()
                userFollowers.text = result.followers.second.toString()
                userScore.text = "$scoreSecond"
            }

            binding.textResult.text = resultMessage
        }
    }

    @SuppressLint("DefaultLocale")
    private fun isValidInput(): Boolean {
        val username1 = binding.fieldUsername1.editText?.text.toString().toLowerCase()
        val username2 = binding.fieldUsername2.editText?.text.toString().toLowerCase()

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
            return false
        }

        return true
    }

    private fun showFieldError(
        inputLayout: TextInputLayout,
        message: String = getString(R.string.message_error_input)
    ) {
        inputLayout.error = message
        binding.buttonPlay.disable()
    }


    private fun playAnimation(view: LottieAnimationView) {
        view.show()
        view.playAnimation()
    }

    private fun hideAnimationView(view: LottieAnimationView) {
        view.cancelAnimation()
        view.invisible()
    }
}