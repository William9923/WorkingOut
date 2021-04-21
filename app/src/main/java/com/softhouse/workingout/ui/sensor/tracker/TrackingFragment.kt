package com.softhouse.workingout.ui.sensor.tracker

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
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
import com.softhouse.workingout.shared.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.softhouse.workingout.shared.TrackingUtility
import com.softhouse.workingout.ui.sensor.compass.CompassFragment
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class TrackingFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var viewModel: TrackingViewModel
    lateinit var binding: FragmentTrackingBinding

    private var started: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            ViewModelProviders.of(requireActivity()).get(TrackingViewModel::class.java)

        // TODO : Pindahin business logic ke view model, terus semua UI listen ke view model
        viewModel.started.observe(viewLifecycleOwner, {
            started = it
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissions()

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
        viewModel = ViewModelProviders.of(requireActivity()).get(TrackingViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

        if (started) {
            startForegroundServiceForSensors(false)
        }
//
    }

    override fun onPause() {
        super.onPause()

        if (started) {
            startForegroundServiceForSensors(true)
        }
//
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
        requiredIntent.action = StepDetectorService.ACTION_START
        requireActivity().startService(requiredIntent)
//        ContextCompat.startForegroundService(requireActivity(), requiredIntent)
    }

    private fun stopForegroundServiceForSensors() {
        val requiredIntent = Intent(requireActivity(), StepDetectorService::class.java)
        requiredIntent.action = StepDetectorService.ACTION_STOP
        requireActivity().stopService(requiredIntent)
//        ContextCompat.startForegroundService(requireActivity(), requiredIntent)
    }

    private fun switchOnSensor() {
        started = true
        Log.d("BUTTON", "Start Action")
        startForegroundServiceForSensors(false)
        binding.actionBtn.text = "STOP"
        // TODO("Change Button Color")
    }

    private fun switchOffSensor() {
        binding.actionBtn.text = "START"
        started = false
        Log.d("BUTTON", "Stop Action")
        stopForegroundServiceForSensors()
        // TODO("Make Navigation to the next fragment)
    }

    private fun requestPermissions() {
        if (TrackingUtility.hasLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    companion object

}