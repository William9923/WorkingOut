package com.softhouse.workingout.ui.sensor.tracker

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.softhouse.workingout.R
import com.softhouse.workingout.databinding.FragmentTrackingBinding
import com.softhouse.workingout.service.GeoTrackerService
import com.softhouse.workingout.service.StepDetectorService
import com.softhouse.workingout.service.StepTrackerService
import com.softhouse.workingout.shared.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.softhouse.workingout.shared.TrackingUtility
import com.softhouse.workingout.shared.roundTo
import com.softhouse.workingout.ui.sensor.compass.CompassFragment
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private val viewModel: TrackingViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )
    lateinit var binding: FragmentTrackingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackingBinding.inflate(inflater, container, false)
        // Make screen orientation always portrait
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // For first time user, request needed permission...
        requestPermissions()

        // Add the child fragment here...
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.parent_fragment_container, CompassFragment()).commit()

        /**
         * Handling Initialization value
         */
        binding.textTrackerMetric.text = if (viewModel.mode.value == Mode.STEPS) "steps" else "km"
        binding.actionBtn.text = if (viewModel.started.value!!) "STOP" else "START"
        binding.switchMode.isChecked = viewModel.mode.value == Mode.STEPS
        binding.switchMode.text = if (viewModel.mode.value == Mode.STEPS) "Running" else "Cycling"

        /**
         * Binding Application Listener
         */

        binding.actionBtn.setOnClickListener {
            if (!viewModel.started.value!!) {
                viewModel.start()
                when (viewModel.mode.value!!) {
                    Mode.CYCLING -> sendLocationCommandToService(GeoTrackerService.ACTION_START_OR_RESUME_SERVICE_GEO)
                    Mode.STEPS -> sendStepCommandToService(StepTrackerService.ACTION_START_OR_RESUME_SERVICE_STEP)
                }
            } else {
                viewModel.stop()
                when (viewModel.mode.value!!) {
                    Mode.CYCLING -> sendLocationCommandToService(GeoTrackerService.ACTION_STOP_SERVICE_GEO)
                    Mode.STEPS -> sendStepCommandToService(StepTrackerService.ACTION_STOP_SERVICE_STEP)
                }
            }
        }

        binding.switchMode.setOnCheckedChangeListener { buttonView, _ ->
            if (!viewModel.started.value!!) {
                viewModel.toggleMode()
            } else {
                Toast.makeText(requireActivity(), "Tracker have been started", Toast.LENGTH_SHORT).show()
                buttonView.toggle()
            }
        }

        viewModel.mode.observe(viewLifecycleOwner, {
            binding.switchMode.text = if (it == Mode.STEPS) "Running" else "Cycling"
            binding.textTrackerMetric.text = if (it == Mode.STEPS) "steps" else "km"
        })

        subscribeToObservers()
    }

    /**
     * Subscribe to LifeCycle Service
     */
    private fun subscribeToObservers() {
        subscribeToObserverGeoTracker()
        subscribeToObserverStepsTracker()
    }

    private fun subscribeToObserverGeoTracker() {
        GeoTrackerService.isTracking.observe(viewLifecycleOwner, {
            if (viewModel.mode.value == Mode.CYCLING) {
                if (viewModel.started.value!! && !it) {
                    viewModel.stop()
                }
                binding.actionBtn.text = if (it) "STOP" else "START"
            }
        })
        GeoTrackerService.pathPoints.observe(viewLifecycleOwner, {
            if (viewModel.mode.value == Mode.CYCLING) {
                viewModel.updateCoordinates(it)
            }
        })
        GeoTrackerService.timeRunInMillis.observe(viewLifecycleOwner, {
            if (viewModel.mode.value == Mode.CYCLING) {
                viewModel.updateDuration(it)
            }
        })
        GeoTrackerService.distance.observe(viewLifecycleOwner, {
            if (viewModel.mode.value == Mode.CYCLING) {
                binding.textTracker.text = ((it * 0.001) * 1.0).roundTo(2).toString()
            }
        })
    }

    private fun subscribeToObserverStepsTracker() {
        StepTrackerService.isTracking.observe(viewLifecycleOwner, {
            if (viewModel.mode.value == Mode.STEPS) {
                if (viewModel.started.value!! && !it) {
                    viewModel.stop()
                }
                binding.actionBtn.text = if (it) "STOP" else "START"
            }
        })
        StepTrackerService.steps.observe(viewLifecycleOwner, {
            if (viewModel.mode.value == Mode.STEPS) {
                viewModel.updateSteps(it)
                binding.textTracker.text = it.toString()
            }
        })
        StepTrackerService.timeRunInMillis.observe(viewLifecycleOwner, {
            if (viewModel.mode.value == Mode.STEPS) {
                viewModel.updateDuration(it)
            }
        })
        StepTrackerService.isSensorAvailable.observe(viewLifecycleOwner, {
            if (!it) {
                Toast.makeText(requireActivity(), "No Step sensor detected! Using accelerometer", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun sendStepCommandToService(action: String) =
        Intent(requireContext(), StepTrackerService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun sendLocationCommandToService(action: String) =
        Intent(requireContext(), GeoTrackerService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    /**
     * Permission Request Function
     */
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