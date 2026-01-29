package hr.algebra.moviedb.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import hr.algebra.moviedb.framework.AlarmHelper

private const val TAG = "BootCompletedReceiver"

/**
 * BroadcastReceiver that handles device boot completion.
 * 
 * When the device boots up, all scheduled alarms are lost. This receiver
 * listens for the BOOT_COMPLETED broadcast and reschedules the movie
 * refresh alarm based on saved user preferences.
 * 
 * Required permission in AndroidManifest.xml:
 * <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
 */
class BootCompletedReceiver : BroadcastReceiver() {
    
    /**
     * Called when the device finishes booting.
     * Reschedules the refresh alarm according to user settings.
     * 
     * @param context The Context in which the receiver is running
     * @param intent The Intent being received (ACTION_BOOT_COMPLETED)
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Device boot completed - rescheduling alarms")
            
            // Reschedule the movie refresh alarm based on saved preferences
            AlarmHelper.updateAlarmFromSettings(context)
            
            Log.d(TAG, "Alarms rescheduled successfully after boot")
        }
    }
}
