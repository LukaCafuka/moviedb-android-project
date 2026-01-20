package hr.algebra.moviedb.framework

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.preference.PreferenceManager
import hr.algebra.moviedb.receiver.RefreshAlarmReceiver

private const val TAG = "AlarmHelper"
private const val ALARM_REQUEST_CODE = 2001

/**
 * Helper object for managing scheduled alarms using AlarmManager.
 * Provides methods to schedule and cancel periodic movie data refresh.
 */
object AlarmHelper {
    
    /**
     * Schedules a repeating alarm for movie data refresh.
     * 
     * @param context Application context
     * @param intervalMillis Time interval between refreshes in milliseconds
     */
    fun scheduleRefreshAlarm(context: Context, intervalMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(context)
        
        // Cancel any existing alarm first
        alarmManager.cancel(pendingIntent)
        
        // Calculate first trigger time (current time + interval)
        val triggerAtMillis = System.currentTimeMillis() + intervalMillis
        
        // Schedule repeating alarm
        // Using setInexactRepeating for better battery efficiency
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            intervalMillis,
            pendingIntent
        )
        
        Log.d(TAG, "Refresh alarm scheduled with interval: ${intervalMillis / 1000 / 60} minutes")
    }
    
    /**
     * Cancels the scheduled refresh alarm.
     * 
     * @param context Application context
     */
    fun cancelRefreshAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(context)
        
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Refresh alarm cancelled")
    }
    
    /**
     * Checks if a refresh alarm is currently scheduled.
     * 
     * @param context Application context
     * @return true if alarm is scheduled, false otherwise
     */
    fun isAlarmScheduled(context: Context): Boolean {
        val intent = Intent(context, RefreshAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent != null
    }
    
    /**
     * Updates the alarm based on current settings.
     * Reads auto_refresh and refresh_interval from SharedPreferences.
     * 
     * @param context Application context
     */
    fun updateAlarmFromSettings(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        
        val autoRefresh = prefs.getBoolean("auto_refresh", true)
        val intervalSeconds = prefs.getString("refresh_interval", "3600")?.toLongOrNull() ?: 3600L
        val intervalMillis = intervalSeconds * 1000
        
        if (autoRefresh) {
            scheduleRefreshAlarm(context, intervalMillis)
        } else {
            cancelRefreshAlarm(context)
        }
    }
    
    /**
     * Creates the PendingIntent for the alarm.
     */
    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, RefreshAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
