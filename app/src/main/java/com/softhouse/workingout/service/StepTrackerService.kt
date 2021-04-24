package com.softhouse.workingout.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.softhouse.workingout.R
import com.softhouse.workingout.data.db.Cycling
import com.softhouse.workingout.data.db.Running
import com.softhouse.workingout.data.repository.MainRepository
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.shared.Constants.INVALID_ID_DB
import com.softhouse.workingout.shared.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class StepTrackerService : LifecycleService(), SensorEventListener {

    /**
     * Service - related variable
     */
    var isFirstRun = true
    var serviceKilled = false
    private val timeRunInSeconds = MutableLiveData<Long>()

    /**
     * Service specific handler / provider client / sensor
     */
    private lateinit var sensorManager: SensorManager

    @Inject
    lateinit var mainRepository: MainRepository

    /**
     * Notification handler
     */
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder
    lateinit var curNotificationBuilder: NotificationCompat.Builder

    private fun postInitialValues() {
        isTracking.value = false
        steps.value = 0
        timeRunInSeconds.value = 0L
        timeRunInMillis.value = 0L

        newDataID.value = INVALID_ID_DB
    }

    override fun onCreate() {
        super.onCreate()

        curNotificationBuilder = baseNotificationBuilder

        postInitialValues()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val hasSensor: Boolean = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)

        if (hasSensor) {
            Log.d("Package:", "Have Step Detector")
        } else {
            Log.d("Package:", "Not Have Step Detector")
        }

        if (hasSensor) {

            val stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

            if (stepDetector == null) {
                Log.d("Detector", "Sensor is null")
            }

            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)?.also { stepDetector ->
                sensorManager.registerListener(
                    this,
                    stepDetector,
                    SensorManager.SENSOR_DELAY_FASTEST,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
            isSensorAvailable.value = true
            Log.d("Sensor", "Sensor available")
        } else {
            // Default sensor using accelerometer
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
                sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
            isSensorAvailable.value = false
            Log.d("Sensor", "Sensor not available")
        }

        isTracking.observe(this, {
            updateStepsTracking(it)
            updateNotificationTrackingState(it)
        })
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (isSensorAvailable.value!!) {
            Log.d("Sensor:", "Steps counted!")
            updateStepsTracking(isTracking.value ?: true)
        } else if (event != null && abs(event.values[0]) > 2) {
            updateStepsTracking(isTracking.value ?: true)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

    /**
     * Timer Related
     */
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private fun startTimer() {
        isTracking.value = true
        timeStarted = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                // time difference between now and timeStarted
                lapTime = System.currentTimeMillis() - timeStarted
                // post the new lapTime
                timeRunInMillis.value = timeRun + lapTime
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.value = (timeRunInSeconds.value ?: 0) + 1
                    lastSecondTimestamp += 1000L
                }
                delay(Constants.TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE_STEP -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                        Log.d("StepService", "Starting service...")
                    } else {
                        Log.d("StepService", "Resuming service...")
                        startTimer()
                    }
                }
                ACTION_STOP_SERVICE_STEP -> {
                    Log.d("StepService", "Stopping service...")
                    endTrackingAndSaveToDB()
                    killService()
                }
                else -> Log.d("StepService", "Unrecognized action")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        isTracking.value = false
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    /**
     * Notification related method
     */

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManagerCompat.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        if (isTracking) {
            val stopIntent = Intent(this, StepTrackerService::class.java).apply {
                action = ACTION_STOP_SERVICE_STEP
            }
            val pendingIntent = PendingIntent.getService(this, 99, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
                isAccessible = true
                set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
            }
            if (!serviceKilled) {
                curNotificationBuilder = baseNotificationBuilder
                    .addAction(R.drawable.ic_notifications_black_24dp, "STOP WORKOUT", pendingIntent)
                notificationManager.notify(Constants.NOTIFICATION_ID, curNotificationBuilder.build())
            }
        }
    }

    /**
     * Background service method
     */

    private fun startForegroundService() {
        startTimer()
        isTracking.value = true

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(Constants.NOTIFICATION_ID, baseNotificationBuilder.build())

        timeRunInSeconds.observe(this, {

            if (!serviceKilled) {
                val notification = curNotificationBuilder
                    .setContentText("You have walked : ${steps.value ?: 0} steps")
                notificationManager.notify(Constants.NOTIFICATION_ID, notification.build())
            }
        })
    }

    /**
     * Step detector related settings
     */
    private fun updateStepsTracking(isTracking: Boolean) {
        if (isTracking) {
            val oldStep = steps.value ?: 0
            steps.value = oldStep + 1
            Log.d("Steps", steps.value.toString())
        }
    }

    private fun endTrackingAndSaveToDB() {
        val running = Running(steps.value ?: 0, timeStarted, lastSecondTimestamp)
        Log.d("Running", running.toString())
        val appScope = CoroutineScope(SupervisorJob())
        // Coroutine : IO Dispatchers because saving to db can wait ...
        appScope.launch(Dispatchers.IO) {
            with(mainRepository) {
                val id = insertRunning(running)
                Log.d("Database", "ID : $id")
                newDataID.postValue(id)
            }
        }
    }

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val steps = MutableLiveData<Int>()
        val timeRunInMillis = MutableLiveData<Long>()
        val isSensorAvailable = MutableLiveData<Boolean>()
        val newDataID = MutableLiveData<Long>()
        const val ACTION_START_OR_RESUME_SERVICE_STEP = "ACTION_START_OR_RESUME_SERVICE_STEP"
        const val ACTION_STOP_SERVICE_STEP = "ACTION_STOP_SERVICE_STEP"
    }
}