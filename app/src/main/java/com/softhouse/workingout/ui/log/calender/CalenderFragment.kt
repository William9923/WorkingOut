package com.softhouse.workingout.ui.log.calender

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.softhouse.workingout.databinding.FragmentCalenderBinding
import com.softhouse.workingout.ui.sensor.tracker.Mode
import com.softhouse.workingout.ui.sensor.tracker.TrackingFragmentDirections
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

        binding.calendarView.setDateSelected(CalendarDay.today(), true)

        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            when (viewModel.mode.value) {
                Mode.CYCLING -> {
                    val action = CalenderFragmentDirections.actionNavigationCalenderToNavigationLogsCycling(
                        date.day,
                        date.month,
                        date.year
                    )
                    NavHostFragment.findNavController(this).navigate(action)
                }
                Mode.STEPS -> {
                    val action = CalenderFragmentDirections.actionNavigationCalenderToNavigationLogsRunning(
                        date.day,
                        date.month,
                        date.year
                    )
                    NavHostFragment.findNavController(this).navigate(action)
                }
            }
        }

        binding.switchModeBtn.setOnClickListener {
            viewModel.toggleMode()
            binding.mode = viewModel.mode.value
        }
    }
}