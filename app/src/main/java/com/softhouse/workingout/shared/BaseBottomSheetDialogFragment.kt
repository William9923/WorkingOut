package com.softhouse.workingout.shared

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.softhouse.workingout.R
import com.softhouse.workingout.alarm.Utils

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    open var alphaAnimationForFragmentTransitionEnabled = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (alphaAnimationForFragmentTransitionEnabled) {
            dialog?.window?.attributes?.windowAnimations = R.style.MyDialogAnimation
        }
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onStart() {
        super.onStart()
        val height =
            (Resources.getSystem().displayMetrics.heightPixels * Utils.HEIGHT_RATIO).toInt()
        dialog?.let {
            val bs = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)!!
            bs.layoutParams.height = height
            with(BottomSheetBehavior.from(bs)) {
                peekHeight = height
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(
            object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        val drawable = createMaterialShapeDrawable(bottomSheet)
                        ViewCompat.setBackground(bottomSheet, drawable)
                    }
                }
            }
        )
        return dialog
    }

    private fun createMaterialShapeDrawable(bottomSheet: View): MaterialShapeDrawable? {
        val model = ShapeAppearanceModel.builder(
            requireContext(),
            0,
            R.style.CustomShapeAppearanceBottomSheetDialog
        ).build()
        val currentDrawable = bottomSheet.background as MaterialShapeDrawable
        val newDrawable = MaterialShapeDrawable(model)
        with(newDrawable) {
            initializeElevationOverlay(requireContext())
            fillColor = currentDrawable.fillColor
            tintList = currentDrawable.tintList
            elevation = currentDrawable.elevation
            strokeWidth = currentDrawable.strokeWidth
            strokeColor = currentDrawable.strokeColor
        }
        return newDrawable
    }
}
