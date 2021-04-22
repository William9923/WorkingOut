package com.softhouse.workingout.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.softhouse.workingout.ui.MainActivity
import com.softhouse.workingout.R
import com.softhouse.workingout.listener.StopNotificationListener
import kotlin.math.abs

class StepDetectorService : Service(), SensorEventListener {

    /**
     * Binding related variable
     */
    private lateinit var sensorManager: SensorManager

    /**
     * Service Related Variable
     */
    private var background = false

    /**
     * Notification Related Variable
     */
    private val notificationActivityRequestCode = 90
    private val notificationId = 91
    private val notificationStopRequestCode = 92

    private var currentStep = 0
    private var isSensorAvailable = true

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager

        val hasSensor: Boolean = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)

        if (hasSensor) {
            Log.d("Package:", "Have Step Detector")
        } else {
            Log.d("Package:", "Not Have Step Detector")
        }

        if (hasSensor) {
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)?.also { stepDetector ->
                sensorManager.registerListener(
                    this,
                    stepDetector,
                    SensorManager.SENSOR_DELAY_NORMAL,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
            isSensorAvailable = true
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
            isSensorAvailable = false
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (isSensorAvailable) {
            // Step Detector
            updateStep()
        } else {
            // Accelerometer
            if (event != null && abs(event.values[0]) > 5) {
                updateStep()
            }
        }
    }

    private fun updateStep() {
        currentStep += 1
        Log.i("Current Step", currentStep.toString())

        val intent = Intent("com.service.StepDetectorService")
        intent.putExtra(KEY_STEP, currentStep)
        intent.action = KEY_ON_SENSOR_CHANGED_ACTION

        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        if (background) {
            val notification = createNotification(currentStep)
            startForeground(notificationId, notification)
        } else {
            stopForeground(true)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var action: String = intent?.action!!

        intent.let {
            background = it.getBooleanExtra(KEY_BACKGROUND, false)
        }

        when (action) {
            ACTION_START -> {
                Log.d("Action", "Start step detector service!")
                startForeground()
            }
            ACTION_STOP -> {
                Log.d("Action", "Stop step detector service!")
                stopForeground(true)
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(step: Int): Notification {

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                application.packageName,
                "Notifications", NotificationManager.IMPORTANCE_DEFAULT
            )

            // Configure the notification channel.
            notificationChannel.enableLights(false)
            notificationChannel.setSound(null, null)
            notificationChannel.enableVibration(false)
            notificationChannel.vibrationPattern = longArrayOf(0L)
            notificationChannel.setShowBadge(false)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(baseContext, application.packageName)
        // Open activity intent
        val contentIntent = PendingIntent.getActivity(
            this, notificationActivityRequestCode,
            Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Stop notification intent
        val stopNotificationIntent = Intent(this, StopNotificationListener::class.java)
        stopNotificationIntent.action = KEY_NOTIFICATION_STOP_ACTION
        stopNotificationIntent.putExtra(KEY_NOTIFICATION_ID, notificationId)
        val pendingStopNotificationIntent =
            PendingIntent.getBroadcast(
                this,
                notificationStopRequestCode,
                stopNotificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText("You have walked : $step")
            .setWhen(System.currentTimeMillis())
            .setDefaults(0)
            .setVibrate(longArrayOf(0L))
            .setSound(null)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(contentIntent)
            .addAction(
                R.mipmap.ic_launcher_round,
                getString(R.string.stop_notifications),
                pendingStopNotificationIntent
            )

        return notificationBuilder.build()
    }

    private fun startForeground() {
        // Start binding notification
        val notification = createNotification(0)
        startForeground(notificationId, notification)
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do Nothing
    }

    companion object {
        const val ACTION_START = "StepDetectorService::START"
        const val ACTION_STOP = "StepDetectorService::STOP"
        const val KEY_STEP = "step"
        const val KEY_BACKGROUND = "background"
        const val KEY_NOTIFICATION_ID = "notificationId"
        const val KEY_ON_SENSOR_CHANGED_ACTION =
            "com.softhouse.workingout.service.StepDetectorService.ON_SENSOR_CHANGED"
        const val KEY_NOTIFICATION_STOP_ACTION = "com.softhouse.workingout.service.NOTIFICATION_STOP"
    }
}