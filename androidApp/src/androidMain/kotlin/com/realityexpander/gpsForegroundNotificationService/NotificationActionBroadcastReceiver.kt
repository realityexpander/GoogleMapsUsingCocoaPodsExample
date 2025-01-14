package com.realityexpander.gpsForegroundNotificationService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidTextToSpeechService
import data.appSettings

// Accepts intents for Actions from the GPS tracking Foreground-Service Notification
class NotificationActionBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Log.i("NotificationActionBroadcastReceiver: onReceive: intent: $intent")
        context ?: return
        intent ?: return

        if(intent.action == GPS_FOREGROUND_SERVICE_NOTIFICATION_StopSpeakingTextToSpeech_ACTION) {
            androidTextToSpeechService?.stopSpeaking()
        }
        if(intent.action == GPS_FOREGROUND_SERVICE_NOTIFICATION_MuteAllTextToSpeech_ACTION) {
            androidTextToSpeechService?.stopSpeaking()
            appSettings.isSpeakWhenUnseenMarkerFoundEnabled = false
        }
    }

    companion object {
        const val GPS_FOREGROUND_SERVICE_NOTIFICATION_StopSpeakingTextToSpeech_ACTION =
            "GPS_FOREGROUND_SERVICE_NOTIFICATION_StopSpeakingTextToSpeech_ACTION"
        const val GPS_FOREGROUND_SERVICE_NOTIFICATION_MuteAllTextToSpeech_ACTION =
            "GPS_FOREGROUND_SERVICE_NOTIFICATION_MuteAllTextToSpeech_ACTION"

        const val kNotificationActionRequestCode = 1
    }
}
