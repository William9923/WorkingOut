package com.softhouse.workingout.ui.schedules

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.softhouse.workingout.R
import com.softhouse.workingout.databinding.FragmentScheduleBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    lateinit var binding: FragmentScheduleBinding

    private val viewModel: ScheduleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
        // Make screen orientation always portrait
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initMode()
        viewModel.initType()

        binding.mode = viewModel.mode.value!!
        binding.types = viewModel.type.value!!

        binding.actionBtn.setOnClickListener {
            Log.d("Picker", "Save Schedule")
        }

        binding.switchModeBtn.setOnClickListener {
            viewModel.toggleMode()
            binding.mode = viewModel.mode.value!!
        }

        binding.switchTypesBtn.setOnClickListener {
            viewModel.toggleType()
            binding.types = viewModel.type.value!!
        }

        binding.startTimeBtn.setOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(10)
                    .build()

            picker.show(requireFragmentManager(), "tag")
            picker.addOnPositiveButtonClickListener {
                Log.d("Picker", "End Button finished")
                binding.startTimeText.text = "${picker.hour}:${picker.minute}"
            }
        }

        binding.endTimeBtn.setOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(10)
                    .build()

            picker.show(requireFragmentManager(), "tag")

            picker.addOnPositiveButtonClickListener {
                Log.d("Picker", "End Button finished")
                binding.endTimeText.text = "${picker.hour}:${picker.minute}"
            }
        }

        binding.dateBtn.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            datePicker.show(requireFragmentManager(), "tag")
            datePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = datePicker.selection ?: 0L
                Log.d(
                    "Picker",
                    "${calendar.get(Calendar.YEAR)} / ${calendar.get(Calendar.MONTH)} / ${calendar.get(Calendar.DAY_OF_MONTH)}"
                )
            }
        }

        binding.chkBoxAuto.setOnCheckedChangeListener { buttonView, isChecked ->
            // Responds to checkbox being checked/unchecked
            if (isChecked) {
                Log.d("Picker", "Auto Timer")
            } else {
                Log.d("Picker", "Manual timer")
            }
        }

        binding.toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            // Respond to button selection
            when (checkedId) {
                R.id.buttonMon -> Log.d("Picker", "Toggle BTN Monday")
                R.id.buttonTues -> Log.d("Picker", "Toggle BTN Tuesday")
                R.id.buttonWed -> Log.d("Picker", "Toggle BTN Wednesday")
                R.id.buttonThurs -> Log.d("Picker", "Toggle BTN Thursday")
                R.id.buttonFri -> Log.d("Picker", "Toggle BTN Friday")
                R.id.buttonSat -> Log.d("Picker", "Toggle BTN Saturday")
                R.id.buttonSun -> Log.d("Picker", "Toggle BTN Sunday")
            }

            if (isChecked) {
                Log.d("Picker", "Activate ID : ${checkedId}")
            }
        }
    }

}