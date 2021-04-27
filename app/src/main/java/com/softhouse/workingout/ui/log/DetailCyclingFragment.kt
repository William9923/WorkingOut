package com.softhouse.workingout.ui.log

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.softhouse.workingout.databinding.FragmentDetailCyclingBinding
import com.softhouse.workingout.shared.Constants.POLYLINE_COLOR
import com.softhouse.workingout.shared.Constants.POLYLINE_WIDTH
import com.softhouse.workingout.shared.DateTimeUtility
import com.softhouse.workingout.shared.Polyline
import com.softhouse.workingout.ui.log.dto.CyclingDTO
import com.softhouse.workingout.ui.log.dto.RunningDTO
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DetailCyclingFragment : Fragment() {

    private val viewModel: DetailCyclingViewModel by viewModels()
    lateinit var binding: FragmentDetailCyclingBinding
    private val args: DetailCyclingFragmentArgs by navArgs()
    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Invoke trigger for appbar menu
        setHasOptionsMenu(true)
        // Setup data for record display
        viewModel.initData(args.recordId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailCyclingBinding.inflate(inflater, container, false)
        // Make screen orientation always portrait
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)

        viewModel.cycling.observe(viewLifecycleOwner, {
            if (it != null) {
                val startCalendar = DateTimeUtility.getCalenderFromMillis(it.startWorkout)
                val endCalender = DateTimeUtility.getCalenderFromMillis(it.endWorkout)
                binding.dto = CyclingDTO(
                    args.recordId,
                    it.distanceInMeters,
                    "${startCalendar.get(Calendar.HOUR_OF_DAY)}:${startCalendar.get(Calendar.MINUTE)}:${
                        startCalendar.get(
                            Calendar.SECOND
                        )
                    }",
                    "${endCalender.get(Calendar.HOUR_OF_DAY)}:${endCalender.get(Calendar.MINUTE)}:${
                        endCalender.get(
                            Calendar.SECOND
                        )
                    }",
                    DateTimeUtility.getTimeMeasurementFromMillis(it.endWorkout - it.startWorkout)
                )

                drawPolylines()
                zoomToSeeWholeTrack()
                markStartEndLocation()

                binding.show = true
            } else {
                binding.show = false
            }
        })

        binding.mapView.getMapAsync {
            map = it
        }
    }

    /**
     * Drawing Options
     */
    private fun drawPolylines() {
        if (viewModel.cycling.value != null) {
            Log.d("Map", "Draw polylines")
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(viewModel.cycling.value!!.points)
            map?.addPolyline(polylineOptions)
        }

    }

    private fun zoomToSeeWholeTrack() {
        if (viewModel.cycling.value != null) {
            Log.d("Map", "Zoom whole track")
            val bounds = LatLngBounds.Builder()
            for (pos in viewModel.cycling.value!!.points) {
                bounds.include(pos)
            }

            map?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds.build(),
                    binding.mapView.width,
                    binding.mapView.height,
                    (binding.mapView.height * 0.05f).toInt()
                )
            )
        }
    }

    private fun markStartEndLocation() {
        if (viewModel.cycling.value != null) {
            Log.d("Map", "Mark start end location")
            val start = viewModel.cycling.value!!.points.firstOrNull()
            val end = viewModel.cycling.value!!.points.lastOrNull()

            if (start != null)
                map?.addMarker(MarkerOptions().position(start).title("Start"))

            if (end != null)
                map?.addMarker(MarkerOptions().position(end).title("Finish"))
        }

    }

    /**
     * MapView State
     */
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}