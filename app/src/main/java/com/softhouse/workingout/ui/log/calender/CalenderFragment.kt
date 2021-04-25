package com.softhouse.workingout.ui.log.calender

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.softhouse.workingout.databinding.FragmentCalenderBinding
import com.softhouse.workingout.ui.sensor.tracker.TrackingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CalenderFragment : Fragment() {

    lateinit var binding: FragmentCalenderBinding
    private val viewModel: TrackingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalenderBinding.inflate(inflater, container, false)
        // Make screen orientation always portrait
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mode = viewModel.mode.value

        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            Log.d("Calender:", "Day : ${date.day}")
            Log.d("Calender:", "Month : ${date.month}")
            Log.d("Calender:", "Month : ${date.year}")
            val calender = Calendar.getInstance()
            calender.set(date.year, date.month, date.day)
            Log.d("Calender:", "Millis : ${calender.timeInMillis}}")

            // TODO : (Fragment Navigation to the new data)
        }

        binding.switchModeBtn.setOnClickListener {
            viewModel.toggleMode()
            binding.mode = viewModel.mode.value
        }
    }
}