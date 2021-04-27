package com.softhouse.workingout.ui.log

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.softhouse.workingout.databinding.FragmentDetailRunningBinding
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.shared.DateTimeUtility
import com.softhouse.workingout.shared.DateTimeUtility.getTimeMeasurementFromMillis
import com.softhouse.workingout.ui.log.dto.RunningDTO
import com.softhouse.workingout.ui.log.list.RunningLogsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DetailRunningFragment : Fragment() {

    private val viewModel: RunningLogsViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )
    lateinit var binding: FragmentDetailRunningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Invoke trigger for appbar menu
        setHasOptionsMenu(true)
        // Setup data for record display
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

        viewModel.running.observe(viewLifecycleOwner, {
            if (it != null) {
                val startCalendar = DateTimeUtility.getCalenderFromMillis(it.startWorkout)
                val endCalender = DateTimeUtility.getCalenderFromMillis(it.endWorkout)

                binding.dto = RunningDTO(
                    it.id ?: Constants.INVALID_ID_DB,
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
                    getTimeMeasurementFromMillis(it.endWorkout - it.startWorkout)
                )
                binding.show = true
            } else {
                binding.show = false
            }
        })
    }
}