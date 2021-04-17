package com.softhouse.workingout.ui.sensor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.softhouse.workingout.R
import com.softhouse.workingout.databinding.FragmentCompassBinding
import com.softhouse.workingout.databinding.FragmentNewsListBinding
import com.softhouse.workingout.service.CompassService


class CompassFragment : Fragment() {

    lateinit var binding: FragmentCompassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, IntentFilter(CompassService.KEY_ON_SENSOR_CHANGED_ACTION))
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompassBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CompassFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val angle = intent.getDoubleExtra(CompassService.KEY_ANGLE, 0.0)

            if (binding != null)
                binding.compassImageView.rotation = angle.toFloat() * -1
        }
    }

    private fun startForegroundServiceForSensors(background: Boolean) {
        val requiredIntent = Intent(requireActivity(), CompassService::class.java)
        requiredIntent.putExtra(CompassService.KEY_BACKGROUND, background)
        ContextCompat.startForegroundService(requireActivity(), requiredIntent)
    }

}