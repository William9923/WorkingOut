package com.softhouse.workingout.ui.sensor.compass

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.softhouse.workingout.databinding.FragmentCompassBinding
import com.softhouse.workingout.service.CompassService


class CompassFragment : Fragment() {

    lateinit var binding: FragmentCompassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

        // Get the broadcast manager, and then register for receiving intent from the CompassService
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, IntentFilter(CompassService.KEY_ON_SENSOR_CHANGED_ACTION))
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), CompassService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        sendCommandToService(CompassService.ACTION_START_OR_RESUME_SERVICE_COMPASS)
    }

    override fun onPause() {
        super.onPause()
        sendCommandToService(CompassService.ACTION_PAUSE_SERVICE_COMPASS)
    }


    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver)
        sendCommandToService(CompassService.ACTION_STOP_SERVICE_COMPASS)
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompassBinding.inflate(inflater, container, false)
        return binding.root
    }


    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val angle = intent.getDoubleExtra(CompassService.KEY_ANGLE, 0.0)
            binding.compassImageView.rotation = angle.toFloat() * -1
        }
    }

    companion object
}