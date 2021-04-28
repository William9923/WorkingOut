package com.softhouse.workingout.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.core.net.toUri
import com.softhouse.workingout.alarm.NOTIFICATION_ID
import com.softhouse.workingout.alarm.NotificationUtil
import com.softhouse.workingout.R
import com.softhouse.workingout.alarm.Utils
import com.softhouse.workingout.listener.AlarmReceiver
import com.softhouse.workingout.alarm.createPendingIntentToActivity
import com.softhouse.workingout.data.Alarm
import com.softhouse.workingout.alarm.isConnectedToInternet
import com.softhouse.workingout.preferences.Preferences
import com.softhouse.workingout.alarm.setLanguageOrDefault
import org.koin.core.component.KoinApiExtension
import timber.log.Timber
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

@KoinApiExtension
class AlarmService : Service(), MediaPlayer.OnPreparedListener {

    companion object {
        private const val VIBRATE_DELAY = 1000L
        private const val VIBRATE_PLAY = 1000L
        private const val VIBRATE_SLEEP = 1000L
        private const val MAX_VOLUME = 100
        private const val VOLUME_SILENT = 0f
        private const val VOLUME_FULL = 1f
        private const val FADE_INTERVAL = 250
        private const val Z_AXIS = 2
        private const val ACC_THRESHOLD = 9
        private const val SPEECH_RATE = 0.8f
    }

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var tts: TextToSpeech? = null
    private var alarm: Alarm? = null
    private var volume = 0f
    private var timer: Timer? = null
    private var enableAccelerometer = Preferences.faceDownToSnooze
    private var enableTts = Preferences.readAlarmTimeLoud

    private val accelerometerListener = object : SensorEventListener {
        private var isFaceUpCompleted = false

        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}

        override fun onSensorChanged(arg0: SensorEvent) {
            val value = arg0.values[Z_AXIS]
            if (value >= ACC_THRESHOLD) {
                isFaceUpCompleted = true
            } else if (isFaceUpCompleted && value <= -ACC_THRESHOLD) {
                sensorManager.unregisterListener(this)
                val intent = Intent(this@AlarmService, AlarmReceiver::class.java).apply {
                    action = AlarmReceiver.ACTION_SNOOZE
                    putExtra(AlarmReceiver.EXTRA_ALARM, alarm?.toBundle())
                }
                isFaceUpCompleted = false
                sendBroadcast(intent)
            }
        }
    }

    private val ttsListener = TextToSpeech.OnInitListener {
        if (it == TextToSpeech.SUCCESS) {
            val res = tts?.setLanguageOrDefault(Locale.US)
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, getString(R.string.tts_not_supported), Toast.LENGTH_SHORT)
                    .show()
            } else if (alarm != null) {
                var hour = alarm!!.hour
                if (hour > Utils.NOON) {
                    hour -= Utils.NOON
                }
                val str = getString(R.string.alarm_speech, hour, alarm!!.minute)
                tts?.speak(str, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        val usage = if (Preferences.useDeviceAlarmVolume) {
            AudioAttributes.USAGE_ALARM
        } else {
            AudioAttributes.USAGE_MEDIA
        }
        val attrs = AudioAttributes.Builder().setUsage(usage).build()
        mediaPlayer.reset()
        mediaPlayer.setAudioAttributes(attrs)
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnErrorListener { _, what, extra ->
            setupMediaPlayerDataAndStart("")
            Timber.e("Service Media Player: what = $what extra: $extra")
            return@setOnErrorListener true
        }
        mediaPlayer.isLooping = true
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        timer = Timer(true)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)
        accelerometer = sensors.firstOrNull()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle = intent?.getBundleExtra(AlarmReceiver.EXTRA_ALARM)
        alarm = Alarm.fromBundle(bundle)
        enableAccelerometer = enableAccelerometer and (alarm?.snooze ?: false)
        enableTts = enableTts and (alarm != null)
        alarm?.let {
            onAlarmGoesOff(it)
            if (enableAccelerometer) {
                sensorManager.registerListener(
                    accelerometerListener,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
            if (enableTts) {
                tts = TextToSpeech(this, ttsListener)
                tts?.setSpeechRate(SPEECH_RATE)
            }
        }
        return START_STICKY
    }

    private fun onAlarmGoesOff(alarm: Alarm) {
        if (alarm.vibrate) {
            val pattern = longArrayOf(VIBRATE_DELAY, VIBRATE_PLAY, VIBRATE_SLEEP)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(pattern, 0)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, 0)
            }
        }
        setupMediaPlayerDataAndStart(alarm.trackUrl)
        val time = alarm.alarmTime
        val formatter = DateTimeFormatter.ofPattern("hh:mm")
        val sb = StringBuilder()
        sb.append(time.format(formatter)).append(" ")
        if (alarm.hour >= Utils.NOON) {
            sb.append(getString(R.string.pm))
        } else {
            sb.append(getString(R.string.am))
        }
        if (alarm.description.isNotEmpty()) {
            sb.append(" ").append(alarm.description)
        }
        val notificationUtil = NotificationUtil(this@AlarmService)
        val notification = notificationUtil.createNotification(
            this@AlarmService,
            sb.toString(),
            getString(R.string.turn_off_alarm_now),
            createPendingIntentToActivity(alarm),
            createSnoozeIntent(alarm),
            createTurnOffIntent(alarm)
        )
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun startFadeIn() {
        volume = VOLUME_SILENT
        mediaPlayer.setVolume(volume, volume)
        val fadeDuration = TimeUnit.SECONDS.toMillis(Preferences.fadeInDuration.toLong())
        var maxVolume = VOLUME_FULL
        if (!Preferences.useDeviceAlarmVolume) {
            maxVolume = VOLUME_FULL * Preferences.customVolume / MAX_VOLUME
        }
        val numberOfSteps = fadeDuration / FADE_INTERVAL
        val deltaVolume = maxVolume / numberOfSteps

        if (timer == null) {
            timer = Timer(true)
        }
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                volume += deltaVolume
                mediaPlayer.setVolume(volume, volume)
                if (volume >= maxVolume) {
                    timer?.cancel()
                    timer?.purge()
                }
            }
        }
        timer?.schedule(timerTask, 0L, FADE_INTERVAL.toLong())
    }

    private fun setupMediaPlayerDataAndStart(url: String) {
        if (url != "" && isConnectedToInternet()) {
            try {
                mediaPlayer.setDataSource(url)
                mediaPlayer.prepareAsync()
            } catch (e: Exception) {
                Timber.e(e)
                setupMediaPlayerDataAndStart("")
            }
            return
        }
        var sound = Preferences.fallbackAudioContentUri?.toUri()
        if (sound == null) {
            sound = RingtoneManager.getActualDefaultRingtoneUri(
                this@AlarmService,
                RingtoneManager.TYPE_ALARM
            )
        }
        if (sound == null) {
            sound = RingtoneManager.getActualDefaultRingtoneUri(
                this@AlarmService,
                RingtoneManager.TYPE_RINGTONE
            )
        }
        try {
            mediaPlayer.setDataSource(this@AlarmService, sound!!)
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onDestroy() {
        if (enableTts) {
            tts?.stop()
            tts?.shutdown()
            tts = null
        }
        if (enableAccelerometer) {
            sensorManager.unregisterListener(accelerometerListener)
        }
        timer?.cancel()
        timer?.purge()
        timer = null
        mediaPlayer.stop()
        mediaPlayer.release()
        vibrator.cancel()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun createSnoozeIntent(alarm: Alarm): PendingIntent? {
        if (!alarm.snooze) return null
        val snoozeIntent = Intent(this.applicationContext, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_SNOOZE
            putExtra(AlarmReceiver.EXTRA_ALARM, alarm.toBundle())
        }
        return PendingIntent.getBroadcast(this, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createTurnOffIntent(alarm: Alarm): PendingIntent {
        val intent = Intent(this.applicationContext, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_TURN_OFF
            putExtra(AlarmReceiver.EXTRA_ALARM, alarm.toBundle())
        }
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onPrepared(player: MediaPlayer?) {
        startFadeIn()
        player?.start()
    }
}
