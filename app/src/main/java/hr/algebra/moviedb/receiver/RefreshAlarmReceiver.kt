package hr.algebra.moviedb.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import hr.algebra.moviedb.api.MovieWorker
import hr.algebra.moviedb.framework.isOnline

private const val TAG = "RefreshAlarmReceiver"
private const val WORK_NAME = "movie_refresh_work"

/**
 * BroadcastReceiver that handles scheduled alarm events for movie data refresh.
 * Uses WorkManager to perform the actual refresh in the background.
 */
class RefreshAlarmReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Alarm received, checking network and scheduling refresh...")
        
        if (context.isOnline()) {
            // Use WorkManager for reliable background work
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    OneTimeWorkRequest.from(MovieWorker::class.java)
                )
            }
            Log.d(TAG, "Refresh work scheduled")
        } else {
            Log.d(TAG, "No network connection, skipping refresh")
        }
    }
}
