package com.softhouse.workingout.ui.sensor.tracker

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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.softhouse.workingout.R
import com.softhouse.workingout.databinding.FragmentTrackingBinding
import com.softhouse.workingout.service.StepDetectorService
import com.softhouse.workingout.ui.sensor.compass.CompassFragment

class TrackingFragment : Fragment() {

    private lateinit var viewModel: TrackingViewModel
    lateinit var binding: FragmentTrackingBinding

    private var started: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        // Get the broadcast manager, and then register for receiving intent from the CompassService
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, IntentFilter(StepDetectorService.KEY_ON_SENSOR_CHANGED_ACTION))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackingBinding.inflate(inflater, container, false)

        viewModel =
            ViewModelProviders.of(this).get(TrackingViewModel::class.java)
        viewModel.started.observe(viewLifecycleOwner, {
            started = it
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add the child fragment here...
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.parent_fragment_container, CompassFragment()).commit()

        // Binding Action Button
        binding.actionBtn.setOnClickListener {

            Log.d("Clicked", "Click Listener working")

            if (!started) {
                switchOnSensor()
            } else {
                switchOffSensor()
            }
        }

        // Binding
        binding.switchMode.setOnCheckedChangeListener { buttonView, isChecked ->

            if (!started) {
                // Change view
                buttonView.text = if (isChecked) "Running" else "Cycling"
                binding.textTrackerMetric.text = if (isChecked) "steps" else "km"

                // Change mode
//                viewModel.mode = if (isChecked) Mode.STEPS else Mode.CYCLING
            } else {
                Toast.makeText(requireActivity(), "Tracker have been started", Toast.LENGTH_SHORT).show()
                buttonView.toggle()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TrackingViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

        if (started) {}
//            startForegroundServiceForSensors(false)
    }

    override fun onPause() {
        super.onPause()

        if (started) {}
//            startForegroundServiceForSensors(true)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val steps = intent.getIntExtra(StepDetectorService.KEY_STEP, 0)
            Log.d("Steps:", steps.toString())
            if (binding != null)
                binding.textTracker.text = steps.toString()
        }
    }

    private fun startForegroundServiceForSensors(background: Boolean) {
        val requiredIntent = Intent(requireActivity(), StepDetectorService::class.java)
        requiredIntent.putExtra(StepDetectorService.KEY_BACKGROUND, background)
        ContextCompat.startForegroundService(requireActivity(), requiredIntent)
    }

    private fun stopForegroundServiceForSensors() {

    }

    private fun switchOnSensor() {
//        startForegroundServiceForSensors(false)
        started = true

        Log.d("BUTTON", "Start Action")

        binding.actionBtn.text = "STOP"
        // TODO("Change Button Color")
    }

    private fun switchOffSensor() {
        binding.actionBtn.text = "START"
        started = false

        Log.d("BUTTON", "Stop Action")
        // TODO("Change Button Color")
    }

//    private fun stopForegroundServiceForSensors() {
//        val requiredIntent = Intent(requireActivity(), StepDetectorService::class.java)
//    }

    companion object

}