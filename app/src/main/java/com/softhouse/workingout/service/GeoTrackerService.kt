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
import com.google.android.gms.maps.model.LatLng
import com.softhouse.workingout.ui.MainActivity
import com.softhouse.workingout.R
import com.softhouse.workingout.shared.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.softhouse.workingout.shared.Constants.NOTIFICATION_CHANNEL_ID
import com.softhouse.workingout.shared.Constants.NOTIFICATION_CHANNEL_NAME
import com.softhouse.workingout.shared.Constants.NOTIFICATION_ID
import com.softhouse.workingout.shared.Constants.TIMER_UPDATE_INTERVAL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.softhouse.workingout.listener.StopNotificationListener
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.shared.Constants.FASTEST_LOCATION_INTERVAL
import com.softhouse.workingout.shared.Constants.LOCATION_UPDATE_INTERVAL
import com.softhouse.workingout.shared.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>

@AndroidEntryPoint
class GeoTrackerService : LifecycleService() {

    /**
     * Service - related variable
     */
    var isFirstRun = true
    private val timeRunInSeconds = MutableLiveData<Long>()

    /**
     * Service specific handler / provider client / sensor
     */
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /**
     * Notification handler
     */
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder
    lateinit var curNotificationBuilder: NotificationCompat.Builder

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()

        curNotificationBuilder = baseNotificationBuilder

        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
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
                        startTimer()
                    }
                }
                ACTION_STOP_SERVICE_GEO -> {
                    Log.d("GeoService", "Stopping service...")
                }
                else -> Log.d("GeoService", "Unrecognized action")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private fun startTimer() {
        addEmptyPolylineIfEmpty()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                // time difference between now and timeStarted
                lapTime = System.currentTimeMillis() - timeStarted
                // post the new lapTime
                timeRunInMillis.postValue(timeRun + lapTime)
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
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
     * Notification related method
     */

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        if (isTracking) {
            val stopIntent = Intent(this, GeoTrackerService::class.java).apply {
                action = ACTION_STOP_SERVICE_GEO
            }
            val pendingIntent = PendingIntent.getService(this, 1, stopIntent, FLAG_UPDATE_CURRENT)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val stopNotificationPendingIntent = PendingIntent.getBroadcast(
                this,
                Constants.NOTIFICATION_STOP_CODE,
                Intent(this, StopNotificationListener::class.java).also {
                    it.action = Constants.ACTION_STOP_NOTIFICATION
                },
                FLAG_UPDATE_CURRENT
            )

            curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
                isAccessible = true
                set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
            }
            curNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_notifications_black_24dp, "STOP", pendingIntent)
                .addAction(
                    R.drawable.ic_notifications_black_24dp,
                    getString(R.string.stop_notifications),
                    stopNotificationPendingIntent
                )
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }

    }

    /**
     * Background service method
     */

    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeRunInSeconds.observe(this, {
            val notification = curNotificationBuilder
                .setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        })
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
                add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolylineIfEmpty() = pathPoints.value.apply {
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf())

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polyline>()
        val timeRunInMillis = MutableLiveData<Long>()
        val timeStarted = MutableLiveData<Long>()
        val lastSecondTimestamp = MutableLiveData<Long>()
        const val ACTION_START_OR_RESUME_SERVICE_GEO = "ACTION_START_OR_RESUME_SERVICE_GEO"
        const val ACTION_PAUSE_SERVICE_GEO = "ACTION_PAUSE_SERVICE_GEO"
        const val ACTION_STOP_SERVICE_GEO = "ACTION_STOP_SERVICE_GEO"
    }
}