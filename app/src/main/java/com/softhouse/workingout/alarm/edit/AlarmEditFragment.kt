package com.softhouse.workingout.alarm.edit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.softhouse.androidutils.EventObserver
import com.softhouse.workingout.R
import com.softhouse.workingout.alarm.Utils
import com.softhouse.workingout.alarm.calculateDurationString
import com.softhouse.workingout.alarm.setup
import com.softhouse.workingout.data.Alarm
import com.softhouse.workingout.databinding.FragmentAlarmEditBinding
import com.softhouse.workingout.shared.BaseBottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinApiExtension
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class AlarmEditFragment : BaseBottomSheetDialogFragment() {

    private var _binding: FragmentAlarmEditBinding? = null
    private val binding: FragmentAlarmEditBinding get() = _binding!!
    override var alphaAnimationForFragmentTransitionEnabled = false

    private val editViewModel: EditViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmEditBinding.inflate(inflater)
        return binding.root
    }

    @KoinApiExtension
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val args = AlarmEditFragmentArgs.fromBundle(it)
            val alarmId = args.alarmId
            editViewModel.retrieveAlarm(alarmId)
        }

        editViewModel.selectedAlarm.observe(viewLifecycleOwner) {
            setupView(!editViewModel.newAlarm, it)
        }

        editViewModel.alarmDateTime.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.durationTextView.text = calculateDurationString(requireContext(), it)
            }
        }

        editViewModel.goToMainPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                navigateToMainFragment()
            }
        )
    }

    private val daysToggleGroupListener =
        MaterialButtonToggleGroup.OnButtonCheckedListener { group, checkedId, isChecked ->
            val button: MaterialButton = group.findViewById(checkedId)
            val index = group.indexOfChild(button)
            editViewModel.updateAlarmDay(index, isChecked)
        }

    @KoinApiExtension
    private fun setupView(exists: Boolean, alarm: Alarm) {
        binding.setup(exists, alarm)
        binding.alarmDateTextView.visibility = VISIBLE
        binding.daysToggleGroup.addOnButtonCheckedListener(daysToggleGroupListener)
        binding.deleteFab.setOnClickListener {
            editViewModel.onDeleteClicked()
        }
        binding.saveFab.setOnClickListener {
            val description = binding.descriptionEditText.text.toString()
            val vibrate = binding.vibrationFab.isChecked
            val snooze = binding.snoozeFab.isChecked
            val target = binding.targetEditText.text.toString().toIntOrNull() ?: 0
            val cycling = binding.modeFab.isChecked
            val autoTrack = binding.autoFab.isChecked
            editViewModel.onSaveClicked(description, vibrate, snooze, target, cycling, autoTrack)
        }
        binding.alarmTimeTextView.setOnClickListener {
            openTimePicker()
        }

        binding.alarmTimeTextView2.setOnClickListener {
            endTimePicker()
        }

        binding.alarmDateTextView.setOnClickListener {
            alarmDatePicker()
        }
        binding.weekDaysChip.setOnClickListener {
            binding.daysToggleGroup.removeOnButtonCheckedListener(daysToggleGroupListener)
            binding.daysToggleGroup.clearChecked()
            for (i in DayOfWeek.MONDAY.ordinal..DayOfWeek.FRIDAY.ordinal) {
                (binding.daysToggleGroup[i] as MaterialButton).isChecked = true
            }
            editViewModel.updateAlarmDay(Alarm.WEEKDAYS)
            binding.daysToggleGroup.addOnButtonCheckedListener(daysToggleGroupListener)
            binding.alarmDateTextView.visibility = GONE
        }
        binding.weekEndsChip.setOnClickListener {
            binding.daysToggleGroup.removeOnButtonCheckedListener(daysToggleGroupListener)
            binding.daysToggleGroup.clearChecked()
            for (i in DayOfWeek.SATURDAY.ordinal..DayOfWeek.SUNDAY.ordinal) {
                (binding.daysToggleGroup[i] as MaterialButton).isChecked = true
            }
            editViewModel.updateAlarmDay(Alarm.WEEKEND)
            binding.daysToggleGroup.addOnButtonCheckedListener(daysToggleGroupListener)
            binding.alarmDateTextView.visibility = GONE
        }
        binding.everydayChip.setOnClickListener {
            binding.daysToggleGroup.removeOnButtonCheckedListener(daysToggleGroupListener)
            binding.daysToggleGroup.clearChecked()
            for (i in DayOfWeek.MONDAY.ordinal..DayOfWeek.SUNDAY.ordinal) {
                (binding.daysToggleGroup[i] as MaterialButton).isChecked = true
            }
            editViewModel.updateAlarmDay(Alarm.EVERYDAY)
            binding.daysToggleGroup.addOnButtonCheckedListener(daysToggleGroupListener)
            binding.alarmDateTextView.visibility = GONE
        }
        binding.onceChip.setOnClickListener {
            binding.daysToggleGroup.removeOnButtonCheckedListener(daysToggleGroupListener)
            binding.daysToggleGroup.clearChecked()
            editViewModel.updateAlarmDay(Alarm.ONCE)
            binding.daysToggleGroup.addOnButtonCheckedListener(daysToggleGroupListener)
            binding.alarmDateTextView.visibility = VISIBLE
        }
        binding.modeFab.setOnClickListener {
            if (binding.modeFab.isChecked) {
                binding.targetTextView.text = getString(R.string.distance)
                binding.modeTextView.text = getString(R.string.cycling)
            }
            else {
                binding.targetTextView.text = getString(R.string.step)
                binding.modeTextView.text = getString(R.string.mode)
            }
        }
    }

    private fun openTimePicker() {
        val now = editViewModel.alarmDateTime.value?.toLocalTime() ?: LocalTime.now()
        val date = editViewModel.alarmDateTime.value?.toLocalDate() ?: LocalDate.now()
        val picker = TimePickerDialog(
            requireContext(),
            R.style.TimePickerDialogTheme,
            { _, hour, minute ->
                val formatter = DateTimeFormatter.ofPattern("hh:mm")
                val time = LocalTime.of(hour, minute)
                binding.alarmTimeTextView.text = time.format(formatter)
                if (hour >= Utils.NOON) {
                    binding.alarmTimePeriodTextView.text = context?.getString(R.string.pm)
                } else {
                    binding.alarmTimePeriodTextView.text = context?.getString(R.string.am)
                }
                editViewModel.updateAlarmTime(hour, minute)
                editViewModel.updateAlarmDate(date.year, date.monthValue, date.dayOfMonth)
            },
            now.hour,
            now.minute,
            false
        )
        picker.show()
    }

    private fun endTimePicker() {
        val now = editViewModel.endDateTime.value?.toLocalTime() ?: LocalTime.now()
        val picker = TimePickerDialog(
            requireContext(),
            R.style.TimePickerDialogTheme,
            { _, hour, minute ->
                val formatter = DateTimeFormatter.ofPattern("hh:mm")
                val time = LocalTime.of(hour, minute)
                binding.alarmTimeTextView2.text = time.format(formatter)
                if (hour >= Utils.NOON) {
                    binding.alarmTimePeriodTextView2.text = context?.getString(R.string.pm)
                } else {
                    binding.alarmTimePeriodTextView2.text = context?.getString(R.string.am)
                }
                editViewModel.updateEndTime(hour, minute)
            },
            now.hour,
            now.minute,
            false
        )
        picker.show()
    }

    private fun alarmDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val now = editViewModel.alarmDateTime.value?.toLocalDate() ?: LocalDate.of(year,month+1,day)
        val time = editViewModel.alarmDateTime.value?.toLocalTime() ?: LocalTime.now()
        val picker = DatePickerDialog(
            requireContext(),
            R.style.TimePickerDialogTheme,
            { _, year, month, day ->
                val formatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy")
                val date = LocalDate.of(year, month+1, day)
                binding.alarmDateTextView.text = date.format(formatter)
                editViewModel.updateAlarmDate(year, month, day)
                editViewModel.updateAlarmTime(time.hour,time.minute)
            },
            now.year,
            now.monthValue-2,
            now.dayOfMonth
        )
        picker.datePicker.minDate = System.currentTimeMillis()
        picker.show()
    }

    private fun navigateToMainFragment() {
        val direction = AlarmEditFragmentDirections.actionAlarmEditFragmentToAlarmFragment()
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
