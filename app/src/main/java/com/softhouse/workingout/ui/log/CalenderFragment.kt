package com.softhouse.workingout.ui.log

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.softhouse.workingout.databinding.FragmentCalenderBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CalenderFragment : Fragment() {

    lateinit var binding: FragmentCalenderBinding
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

        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            Log.d("Calender:", "Day : ${date.day}")
            Log.d("Calender:", "Month : ${date.month}")
            Log.d("Calender:", "Month : ${date.year}")
            val calender = Calendar.getInstance()
            calender.set(date.year, date.month, date.day)
            Log.d("Calender:", "Millis : ${calender.timeInMillis}}")

            // TODO : (Fragment Navigation to the new data)
        }
    }
}