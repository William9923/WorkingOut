package com.softhouse.workingout.ui.schedules

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.softhouse.workingout.R
import com.softhouse.workingout.databinding.FragmentScheduleBinding
import com.softhouse.workingout.service.ScheduleService
import com.softhouse.workingout.ui.sensor.tracker.Mode
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    lateinit var binding: FragmentScheduleBinding

    val dayMap = mutableMapOf(
        "Mon" to false,
        "Tues" to false,
        "Wed" to false,
        "Thurs" to false,
        "Fri" to false,
        "Sat" to false,
        "Sun" to false
    )

    var mode: Mode = Mode.STEPS
    var type: Types = Types.SINGLE
    var autoStart: Boolean = true

    var startTime: Calendar? = null
    var endTime: Calendar? = null
    var target: Long = 0

    var date: Calendar? = null

    lateinit var scheduleService: ScheduleService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleService = ScheduleService(requireContext())
    }

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

        binding.mode = mode
        binding.types = type

        binding.actionBtn.setOnClickListener {
            if (isFilled()) {
                Log.d("Fragment", "Action Button clicked!")
                when (type) {
                    Types.SINGLE -> scheduleService.setSingleAlarm(date!!, startTime!!, endTime!!)
                }
            } else {
                Toast.makeText(requireContext(), "Time not filled", Toast.LENGTH_SHORT).show()
            }
        }

        binding.switchModeBtn.setOnClickListener {
            toggleMode()
            binding.mode = mode
        }

        binding.switchTypesBtn.setOnClickListener {
            toggleType()
            binding.types = type
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

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
                calendar.set(Calendar.MINUTE, picker.minute)
                startTime = calendar
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

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
                calendar.set(Calendar.MINUTE, picker.minute)
                endTime = calendar
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
                date = calendar
            }
        }

        binding.chkBoxAuto.isChecked = autoStart
        binding.chkBoxAuto.setOnCheckedChangeListener { _, isChecked ->
            autoStart = isChecked
        }

        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            // TODO : For repeating week
            when (checkedId) {
                R.id.buttonMon -> toggleDay("Mon", isChecked)
                R.id.buttonTues -> toggleDay("Tues", isChecked)
                R.id.buttonWed -> toggleDay("Wed", isChecked)
                R.id.buttonThurs -> toggleDay("Thurs", isChecked)
                R.id.buttonFri -> toggleDay("Fri", isChecked)
                R.id.buttonSat -> toggleDay("Sat", isChecked)
                R.id.buttonSun -> toggleDay("Sun", isChecked)
            }
        }
    }

    private fun toggleMode() {
        mode = when (mode) {
            Mode.STEPS -> Mode.CYCLING
            Mode.CYCLING -> Mode.STEPS
        }
    }

    private fun toggleType() {
        type = when (type) {
            Types.SINGLE -> Types.REPEATING
            Types.REPEATING -> Types.REPEATING_WEEK
            Types.REPEATING_WEEK -> Types.SINGLE
        }
    }

    private fun toggleDay(dayName: String, value: Boolean) {
        dayMap[dayName] = value
    }

    private fun isFilled(): Boolean {
        val additional: Boolean = if (type == Types.SINGLE) date != null else true
        return (startTime != null && endTime != null && additional)
    }

}