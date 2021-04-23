package com.softhouse.workingout.shared

import androidx.fragment.app.Fragment
import kotlin.math.pow
import kotlin.math.roundToInt

fun Fragment.addChildFragment(fragment: Fragment, frameId: Int) {

    val transaction = childFragmentManager.beginTransaction()
    transaction.replace(frameId, fragment).commit()
}

fun Double.roundTo(numFractionDigits: Int): Double {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return (this * factor).roundToInt() / factor
}