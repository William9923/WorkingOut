package com.softhouse.workingout.ui.sensor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.softhouse.workingout.R
import com.softhouse.workingout.databinding.FragmentCompassBinding
import com.softhouse.workingout.databinding.FragmentRunningBinding
import com.softhouse.workingout.service.CompassService
import com.softhouse.workingout.service.StepCounterService
import com.softhouse.workingout.shared.addChildFragment

class RunningFragment : Fragment() {

    private lateinit var viewModel: RunningViewModel
    lateinit var binding: FragmentRunningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

        // Get the broadcast manager, and then register for receiving intent from the CompassService
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, IntentFilter(CompassService.KEY_ON_SENSOR_CHANGED_ACTION))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRunningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Add the child fragment here...
        val fieldFragment = CompassFragment()
        addChildFragment(fieldFragment, R.id.parent_fragment_container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RunningViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        startForegroundServiceForSensors(false)
    }

    override fun onPause() {
        super.onPause()
        startForegroundServiceForSensors(true)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val steps = intent.getFloatExtra(StepCounterService.KEY_STEP, 0f)
            Log.d("Steps:", steps.toString())
            if (binding != null)
                binding.textSteps.text = steps.toString()
        }
    }

    private fun startForegroundServiceForSensors(background: Boolean) {
        val requiredIntent = Intent(requireActivity(), StepCounterService::class.java)
        requiredIntent.putExtra(StepCounterService.KEY_BACKGROUND, background)
        ContextCompat.startForegroundService(requireActivity(), requiredIntent)
    }

    companion object

}