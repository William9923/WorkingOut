package com.softhouse.workingout.service

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlin.math.round


class CompassService : LifecycleService(), SensorEventListener {

    /**
     * Binding related variable
     */
    private lateinit var sensorManager: SensorManager

    /**
     * Mathematics Related sensor variable
     */
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager

        // Setting up accelerometer sensor
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        // Setting up magnetic field
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }
        updateOrientationAngles()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do Nothing
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE_COMPASS -> {
                    Log.d("StartServiceCompass", "Start / Resume...")
                }
                ACTION_PAUSE_SERVICE_COMPASS -> {
                    Log.d("StartServiceCompass", "Pause...")
                }
                ACTION_STOP_SERVICE_COMPASS -> {
                    Log.d("StartServiceCompass", "Stop...")
                }
                else -> Log.d("StartServiceCompass", "Else...")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)

        val orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles)
        val degrees = (Math.toDegrees(orientation[0].toDouble()) + 360.0) % 360.0
        val angle = round(degrees * 100) / 100

        val intent = Intent("com.service.CompassService")
        intent.putExtra(KEY_ANGLE, angle)
        intent.action = KEY_ON_SENSOR_CHANGED_ACTION

        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

    }

    companion object {
        const val KEY_ANGLE = "angle"
        const val ACTION_START_OR_RESUME_SERVICE_COMPASS = "ACTION_START_OR_RESUME_SERVICE_COMPASS"
        const val ACTION_PAUSE_SERVICE_COMPASS = "ACTION_PAUSE_SERVICE_COMPASS"
        const val ACTION_STOP_SERVICE_COMPASS = "ACTION_STOP_SERVICE_COMPASS"
        const val KEY_ON_SENSOR_CHANGED_ACTION = "com.softhouse.workingout.service.CompassService.ON_SENSOR_CHANGED"
    }
}