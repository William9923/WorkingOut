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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.softhouse.workingout.R
import com.softhouse.workingout.databinding.FragmentTrackingBinding
import com.softhouse.workingout.service.GeoTrackerService
import com.softhouse.workingout.service.StepTrackerService
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.shared.Constants.INVALID_ID_DB
import com.softhouse.workingout.shared.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.softhouse.workingout.shared.TrackingUtility
import com.softhouse.workingout.shared.roundTo
import com.softhouse.workingout.ui.sensor.compass.CompassFragment
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

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
        populateChildFragment()

        // Subscribe to service
        subscribeToObservers()

        // Initializing application state
        viewModel.initMode(checkModeIfStarted())

        // Initializing First time binding value
        binding.mode = viewModel.mode.value!!
        binding.started = isTrackingStarted()

        // Initializing listener for UI Event
        binding.actionBtn.setOnClickListener {
            if (!isTrackingStarted()) {
                when (viewModel.mode.value!!) {
                    Mode.CYCLING -> sendLocationCommandToService(GeoTrackerService.ACTION_START_OR_RESUME_SERVICE_GEO)
                    Mode.STEPS -> sendStepCommandToService(StepTrackerService.ACTION_START_OR_RESUME_SERVICE_STEP)
                }
            } else {
                when (viewModel.mode.value!!) {
                    Mode.CYCLING -> sendLocationCommandToService(GeoTrackerService.ACTION_STOP_SERVICE_GEO)
                    Mode.STEPS -> sendStepCommandToService(StepTrackerService.ACTION_STOP_SERVICE_STEP)
                }
            }
        }

        binding.switchMode.setOnCheckedChangeListener { buttonView, _ ->
            if (!isTrackingStarted()) {
                viewModel.toggleMode()
                binding.mode = viewModel.mode.value!!
            } else {
                Log.d("Switch", "Hid Checked Change Listener")
                Snackbar.make(requireView(), "Tracker have been started!", Snackbar.LENGTH_SHORT)
                    .show()
                buttonView.toggle()
            }
        }
    }

    /**
     * Subscribe to LifeCycle Service
     */
    private fun subscribeToObservers() {
        subscribeToObserverGeoTracker()
        subscribeToObserverStepsTracker()
    }

    private fun isTrackingStarted(): Boolean {
        val isGeoTracking = GeoTrackerService.isTracking.value ?: false
        val isStepTracking = StepTrackerService.isTracking.value ?: false
        return (isGeoTracking || isStepTracking)
    }

    private fun checkModeIfStarted(): Mode {
        // Default Mode : Mode.STEPS
        if (isTrackingStarted()) {
            return when {
                (GeoTrackerService.isTracking.value ?: false) -> Mode.CYCLING
                (StepTrackerService.isTracking.value ?: false) -> Mode.STEPS
                else -> Mode.STEPS
            }
        }
        return Mode.STEPS
    }

    private fun subscribeToObserverGeoTracker() {
        GeoTrackerService.isTracking.observe(viewLifecycleOwner, {
            Log.d("Init", "Observing GeoTracker : $it")
            if (viewModel.mode.value == Mode.CYCLING) {
                binding.started = isTrackingStarted()
            }
        })
        GeoTrackerService.distance.observe(viewLifecycleOwner, {
            if (viewModel.mode.value == Mode.CYCLING) {
                binding.textTracker.text = ((it * 0.001) * 1.0).roundTo(2).toString()
            }
        })
        GeoTrackerService.newDataID.observe(viewLifecycleOwner, {
            Log.d("Receive Data", "ID : $it")
            if (viewModel.mode.value == Mode.CYCLING && it != INVALID_ID_DB && !isTrackingStarted()) {
                val action = TrackingFragmentDirections.actionNavigationTrackingToNavigationCyclingDetail(it)
                NavHostFragment.findNavController(this).navigate(action)
                GeoTrackerService.newDataID.value = INVALID_ID_DB
            }
        })
    }

    private fun subscribeToObserverStepsTracker() {
        StepTrackerService.isTracking.observe(viewLifecycleOwner, {
            Log.d("Init", "Observing StepTracker : $it")
            if (viewModel.mode.value == Mode.STEPS) {
                binding.started = isTrackingStarted()
            }
        })
        StepTrackerService.steps.observe(viewLifecycleOwner, {
            if (viewModel.mode.value == Mode.STEPS) {
                binding.textTracker.text = it.toString()
            }
        })
        StepTrackerService.newDataID.observe(viewLifecycleOwner, {
            Log.d("Receive Data", "ID : $it")
            if (viewModel.mode.value == Mode.STEPS && it != INVALID_ID_DB && !isTrackingStarted()) {
                val action = TrackingFragmentDirections.actionNavigationTrackingToNavigationRunningDetail(it)
                NavHostFragment.findNavController(this).navigate(action)
                StepTrackerService.newDataID.value = INVALID_ID_DB
            }
        })
        StepTrackerService.isSensorAvailable.observe(viewLifecycleOwner, {
            if (!it) {
                Snackbar.make(requireView(), "No Step sensor detected! Using accelerometer", Snackbar.LENGTH_SHORT)
                    .setAction("Dismiss") {
                        // Do Nothing
                    }
                    .show()
            }
        })
    }

    /**
     * Intent Command
     */

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
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION
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

    /**
     * Child Fragment
     */
    private fun populateChildFragment() {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.parent_fragment_container, CompassFragment()).commit()
    }

    companion object

}