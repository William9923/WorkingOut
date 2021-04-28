package com.softhouse.workingout.ui.log.list

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.softhouse.workingout.R
import com.softhouse.workingout.databinding.FragmentDetailRunningBinding
import com.softhouse.workingout.databinding.FragmentDetailRunningPaneBinding
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.shared.DateTimeUtility
import com.softhouse.workingout.ui.log.DetailRunningViewModel
import com.softhouse.workingout.ui.log.dto.RunningDTO
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class DetailRunningPaneFragment(val id: Long = Constants.INVALID_ID_DB) : Fragment() {

    lateinit var binding: FragmentDetailRunningPaneBinding

    private val viewModel: DetailRunningViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Invoke trigger for appbar menu
        setHasOptionsMenu(true)
        // Setup data for record display
        viewModel.initData(id)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailRunningPaneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.show = false
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
                    DateTimeUtility.getTimeMeasurementFromMillis(it.endWorkout - it.startWorkout)
                )
                binding.show = true
                Log.d("SHOW" , "NOT NULL DATA")
            } else {
                Log.d("SHOW" , "NULL DATA")
                binding.show = false
            }
        })
    }

    companion object
}