package com.softhouse.androidutils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewAnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlin.math.hypot

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(
        object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // no-op
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // no-op
            }
        }
    )
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.startColorAnimation(startColor: Int, endColor: Int, duration: Long) {
    val animator = ValueAnimator()
    animator.setIntValues(startColor, endColor)
    animator.setEvaluator(ArgbEvaluator())
    animator.addUpdateListener {
        this.setBackgroundColor(it.animatedValue as Int)
    }
    animator.duration = duration
    animator.start()
}

fun View.startCircularReveal(
    startColor: Int,
    endColor: Int,
    posX: Int? = null,
    posY: Int? = null,
    duration: Long = 1000
) {
    addOnLayoutChangeListener(
        object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                v.removeOnLayoutChangeListener(this)
                val cx = posX ?: (v.left + v.right) / 2
                val cy = posY ?: v.bottom
                val r = hypot(right.toDouble(), bottom.toDouble()).toInt()
                ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, r.toFloat()).apply {
                    interpolator = FastOutSlowInInterpolator()
                    this.duration = duration / 2
                    start()
                }
                startColorAnimation(startColor, endColor, duration / 2)
            }
        }
    )
}

fun View.exitCircularReveal(
    exitX: Int,
    exitY: Int,
    startColor: Int,
    endColor: Int,
    duration: Long = 400,
    block: () -> Unit
) {
    val startRadius = hypot(this.width.toDouble(), this.height.toDouble())
    ViewAnimationUtils.createCircularReveal(this, exitX, exitY, startRadius.toFloat(), 0f).apply {
        this.duration = duration
        interpolator = FastOutSlowInInterpolator()
        addListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    isVisible = false
                    block()
                }
            }
        )
        start()
    }
    startColorAnimation(startColor, endColor, duration)
}

fun View.findLocationOfCenterOnTheScreen(): IntArray {
    val positions = intArrayOf(0, 0)
    getLocationInWindow(positions)
    // Get the center of the view
    positions[0] = positions[0] + width / 2
    positions[1] = positions[1] + height / 2
    return positions
}

fun EditText.moveCursorToEnd() {
    setSelection(text.length)
}
