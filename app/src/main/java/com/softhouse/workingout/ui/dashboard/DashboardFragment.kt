package com.softhouse.workingout.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.softhouse.workingout.R
import com.softhouse.workingout.databinding.FragmentDashboardBinding
import com.softhouse.workingout.databinding.FragmentTrackingBinding
import com.softhouse.workingout.ui.sensor.tracker.TrackingViewModel

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    lateinit var binding: FragmentDashboardBinding
    private var map: GoogleMap? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)

        binding.mapView.getMapAsync {
            map = it
        }
    }

//    private fun subscribeToObservers() {
//        GeoTrackerService.pathPoints.observe(viewLifecycleOwner, Observer {
//            pathPoints = it
//            addLatestPolyline()
//            moveCameraToUser()
//        })
//    }
//
//    private fun moveCameraToUser() {
//        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
//            map?.animateCamera(
//                CameraUpdateFactory.newLatLngZoom(
//                    pathPoints.last().last(),
//                    MAP_ZOOM
//                )
//            )
//        }
//    }
//
//    private fun addAllPolylines() {
//        for(polyline in pathPoints) {
//            val polylineOptions = PolylineOptions()
//                .color(POLYLINE_COLOR)
//                .width(POLYLINE_WIDTH)
//                .addAll(polyline)
//            map?.addPolyline(polylineOptions)
//        }
//    }

    /**
     * Map instance on application callbacks
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