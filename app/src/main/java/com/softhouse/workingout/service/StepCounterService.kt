package com.softhouse.workingout.service

import android.app.*
import android.content.Context
import android.content.Intent
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
import com.softhouse.workingout.MainActivity
import com.softhouse.workingout.R

class StepCounterService : Service(), SensorEventListener {

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
    private val notificationActivityRequestCode = 0
    private val notificationId = 1
    private val notificationStopRequestCode = 2

    private var currentStep = 0f

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager

        // register listener
        Log.d("Sensor", "Step Sensor")

        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.also { stepCounter ->
            sensorManager.registerListener(
                this,
                stepCounter,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        
        Log.d("Sensor", "Finish Setup")

        // Start binding notification
        val notification = createNotification(0f)
        startForeground(notificationId, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.firstOrNull()?.let {
            Log.d("Step", "Step count: $it ")
            currentStep = it
            updateStep()
        }
    }

    private fun updateStep() {

        val intent = Intent("com.service.StepCounterService")
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

        intent?.let {
            background = it.getBooleanExtra(KEY_BACKGROUND, false)
        }

        return START_STICKY
    }

    private fun createNotification(step: Float): Notification {

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


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do Nothing
    }

    companion object {
        const val KEY_STEP = "step"
        const val KEY_BACKGROUND = "background"
        const val KEY_NOTIFICATION_ID = "notificationId"
        const val KEY_ON_SENSOR_CHANGED_ACTION = "com.softhouse.workingout.service.ON_SENSOR_CHANGED"
        const val KEY_NOTIFICATION_STOP_ACTION = "com.softhouse.workingout.service.NOTIFICATION_STOP"
    }
}