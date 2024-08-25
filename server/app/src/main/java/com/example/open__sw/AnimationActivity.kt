package com.example.open__sw

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class AnimationActivity(
    private val transitionMode: TransitionMode = TransitionMode.NONE
) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createTransitionAnimation()
    }

    override fun finish() {
        super.finish()
        exitTransitionAnimation()
    }

    private fun exitTransitionAnimation() {
        val exitAnimResId = when (transitionMode) {
            TransitionMode.HORIZON -> R.anim.slideout_horizon
            TransitionMode.VERTICAL -> R.anim.slideout_vertical
            else -> return
        }

        applyAnimationClose(R.anim.none, exitAnimResId)
    }

    private fun createTransitionAnimation() {
        val enterAnimResId = when (transitionMode) {
            TransitionMode.HORIZON -> R.anim.slidein_horizon
            TransitionMode.VERTICAL -> R.anim.slidein_vertical
            else -> return
        }

        applyAnimationOpen(enterAnimResId, R.anim.none)
    }


    private fun applyAnimationOpen(enterResId: Int, exitResId: Int) {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_OPEN, enterResId, exitResId
            )
        } else {
            overridePendingTransition(enterResId, exitResId)
        }
    }

    private fun applyAnimationClose(enterResId: Int, exitResId: Int) {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_CLOSE, enterResId, exitResId
            )
        } else {
            overridePendingTransition(enterResId, exitResId)
        }
    }

    enum class TransitionMode {
        NONE,
        HORIZON,
        VERTICAL
    }
}