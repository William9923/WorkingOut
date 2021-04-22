package com.softhouse.workingout.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW
import androidx.lifecycle.LifecycleService
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.softhouse.workingout.MainActivity
import com.softhouse.workingout.R
import com.softhouse.workingout.shared.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.softhouse.workingout.shared.Constants.NOTIFICATION_CHANNEL_ID
import com.softhouse.workingout.shared.Constants.NOTIFICATION_CHANNEL_NAME
import com.softhouse.workingout.shared.Constants.NOTIFICATION_ID
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.softhouse.workingout.shared.Constants.FASTEST_LOCATION_INTERVAL
import com.softhouse.workingout.shared.Constants.LOCATION_UPDATE_INTERVAL
import com.softhouse.workingout.shared.TrackingUtility

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class GeoTrackerService : LifecycleService() {

    var isFirstRun = true

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE_GEO -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                        Log.d("GeoService", "Starting service...")
                    } else {
                        Log.d("GeoService", "Resuming service...")
                        resumeService()
                    }
                }
                ACTION_PAUSE_SERVICE_COMPASS_GEO -> {
                    Log.d("GeoService", "Paused service...")
                    pauseService()
                }
                ACTION_STOP_SERVICE_COMPASS_GEO -> {
                    Log.d("GeoService", "Stopping service...")
                }
                else -> Log.d("GeoService", "Unrecognized action")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Background service method
     */

    private fun startForegroundService() {
        addEmptyPolyline()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.bike)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())
        // TODO : add intent for stopping the notification -> can copy from stepdetector

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun pauseService() {
        isTracking.postValue(false)
    }

    private fun resumeService() {
        isTracking.postValue(true)
    }

    /**
     * Google Map API Location Service section
     */

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        Log.d("GeoTrackerService:", "NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
        const val ACTION_START_OR_RESUME_SERVICE_GEO = "ACTION_START_OR_RESUME_SERVICE_GEO"
        const val ACTION_PAUSE_SERVICE_COMPASS_GEO = "ACTION_PAUSE_SERVICE_COMPASS_GEO"
        const val ACTION_STOP_SERVICE_COMPASS_GEO = "ACTION_STOP_SERVICE_COMPASS_GEO"
    }
}