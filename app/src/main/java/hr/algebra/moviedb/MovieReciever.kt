package hr.algebra.moviedb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hr.algebra.moviedb.framework.setBooleanPreference
import hr.algebra.moviedb.framework.startActivity

class MovieReciever : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.setBooleanPreference(DATA_IMPORTED)
        context.startActivity<HostActivity>()    }
}