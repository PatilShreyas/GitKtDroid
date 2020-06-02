package dev.shreyaspatil.ktdroid.utils

import android.animation.Animator
import com.airbnb.lottie.LottieAnimationView

fun LottieAnimationView.setOnAnimationEnd(onEnd: (LottieAnimationView) -> Unit) {
    addAnimatorListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {
            onEnd(this@setOnAnimationEnd)
        }

        override fun onAnimationCancel(animation: Animator?) {}

        override fun onAnimationStart(animation: Animator?) {}
    })
}