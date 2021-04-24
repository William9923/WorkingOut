package com.softhouse.workingout.ui.log

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.softhouse.workingout.shared.Polyline
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailCyclingFragment : Fragment() {

    private val viewModel: DetailCyclingViewModel by viewModels()

    lateinit var binding: FragmentDetailCyclingBinding

    private var pathPoints: Polyline = mutableListOf<LatLng>()

    private var map: GoogleMap? = null

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        drawPolylines()

        zoomToSeeWholeTrack()

        // TODO : Add Marker for first track and last track
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentDetailCyclingBinding.inflate(inflater, container, false)
        // Make screen orientation always portrait
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)

        binding.mapView.getMapAsync {
            callback
        }
    }

    /**
     * Drawing Options
     */
    private fun drawPolylines() {
        val polylineOptions = PolylineOptions()
            .color(POLYLINE_COLOR)
            .width(POLYLINE_WIDTH)
            .addAll(pathPoints)
        (binding.mapView as GoogleMap).addPolyline(polylineOptions)
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (pos in pathPoints) {
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