package com.softhouse.workingout.ui.log

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.softhouse.workingout.R
import com.softhouse.workingout.databinding.FragmentDetailRunningBinding
import com.softhouse.workingout.databinding.FragmentTrackingBinding
import com.softhouse.workingout.service.GeoTrackerService
import com.softhouse.workingout.service.StepTrackerService
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.shared.DateUtility
import com.softhouse.workingout.shared.TrackingUtility
import com.softhouse.workingout.ui.log.dto.RunningDTO
import com.softhouse.workingout.ui.news.WebFragmentArgs
import com.softhouse.workingout.ui.sensor.tracker.Mode
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class DetailRunningFragment : Fragment() {

    private val viewModel: DetailRunningViewModel by viewModels()
    lateinit var binding: FragmentDetailRunningBinding
    private val args: DetailRunningFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Invoke trigger for appbar menu
        setHasOptionsMenu(true)
        // Setup url for webview
        viewModel.initData(args.recordId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailRunningBinding.inflate(inflater, container, false)
        // Make screen orientation always portrait
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.running.value != null) {
            val startCalendar = DateUtility.getCalenderFromMillis(viewModel.running.value!!.startWorkout)
            val endCalender = DateUtility.getCalenderFromMillis(viewModel.running.value!!.endWorkout)
            binding.dto = RunningDTO(
                args.recordId,
                viewModel.running.value!!.steps,
                "${startCalendar.get(Calendar.HOUR_OF_DAY)}:${startCalendar.get(Calendar.MINUTE)}:${
                    startCalendar.get(
                        Calendar.SECOND
                    )
                }",
                "${endCalender.get(Calendar.HOUR_OF_DAY)}:${endCalender.get(Calendar.MINUTE)}:${endCalender.get(Calendar.SECOND)}",
                TimeUnit.MILLISECONDS.toSeconds(viewModel.running.value!!.endWorkout - viewModel.running.value!!.startWorkout)
            )
        }

        viewModel.running.observe(viewLifecycleOwner, {
            if (it != null) {
                val startCalendar = DateUtility.getCalenderFromMillis(it.startWorkout)
                val endCalender = DateUtility.getCalenderFromMillis(it.endWorkout)
                binding.dto = RunningDTO(
                    args.recordId,
                    it.steps,
                    "${startCalendar.get(Calendar.HOUR_OF_DAY)}:${startCalendar.get(Calendar.MINUTE)}:${
                        startCalendar.get(
                            Calendar.SECOND
                        )
                    }",
                    "${endCalender.get(Calendar.HOUR_OF_DAY)}:${endCalender.get(Calendar.MINUTE)}:${
                        endCalender.get(
                            Calendar.SECOND
                        )
                    }",
                    TimeUnit.MILLISECONDS.toSeconds(it.endWorkout - it.startWorkout)
                )
            }
        })
    }
}