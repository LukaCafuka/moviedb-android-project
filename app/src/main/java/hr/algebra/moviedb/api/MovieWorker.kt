package hr.algebra.moviedb.api

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class MovieWorker(private val context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {
    override fun doWork(): Result {
        MovieFetcher(context).fetchItems()
        return Result.success()
    }
}