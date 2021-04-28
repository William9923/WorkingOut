package com.softhouse.workingout.preferences

import com.chibatching.kotpref.KotprefModel

object Preferences : KotprefModel() {
    var readAlarmTimeLoud by booleanPref(default = false)
    var faceDownToSnooze by booleanPref(default = false)
    var slideToTurnOff by booleanPref(default = true)
    var snoozeDuration by intPref(default = 10)
    var fadeInDuration by intPref(default = 10)
    var useDeviceAlarmVolume by booleanPref(default = true)
    var customVolume by intPref(default = 50)
    var fallbackAudioContentUri by nullableStringPref(default = null)
    var listenOffline by booleanPref(default = false)
}
