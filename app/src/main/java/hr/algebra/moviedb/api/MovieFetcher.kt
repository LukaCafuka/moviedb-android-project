package hr.algebra.moviedb.api

import android.content.ContentValues
import android.content.Context
import android.util.Log
import hr.algebra.moviedb.MOVIE_PROVIDER_CONTENT_URI
import hr.algebra.moviedb.MovieReciever
import hr.algebra.moviedb.framework.NotificationHelper
import hr.algebra.moviedb.framework.sendBroadcast
import hr.algebra.moviedb.handler.download
import hr.algebra.moviedb.model.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieFetcher(private val context: Context) {

    private val movieApi: MovieApi
    
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        movieApi = retrofit.create(MovieApi::class.java)
    }

    fun fetchItems(page: Int = 1) {
        val request = movieApi.fetchPopularMovies(page = page)

        request.enqueue(object : Callback<TmdbResponse> {
            override fun onResponse(
                call: Call<TmdbResponse>,
                response: Response<TmdbResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { populateItems(it) }
                } else {
                    Log.e("MovieFetcher", "API Error: ${response.code()} - ${response.message()}")
                    // Still send broadcast to proceed to main screen
                    context.sendBroadcast<MovieReciever>()
                }
            }

            override fun onFailure(
                call: Call<TmdbResponse>,
                t: Throwable
            ) {
                Log.e("MovieFetcher", "Network Error: ${t.message}", t)
                // Still send broadcast to proceed to main screen
                context.sendBroadcast<MovieReciever>()
            }
        })
    }

    private fun populateItems(movieItems: List<MovieItem>) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            var insertedCount = 0
            movieItems.forEach { movie ->
                // Build full poster URL from TMDB
                val posterUrl = movie.posterPath?.let { IMAGE_BASE_URL + it }
                val posterPath = posterUrl?.let { download(context, it) }

                val values = ContentValues().apply {
                    put(Item::title.name, movie.title)
                    put(Item::overview.name, movie.overview)
                    put(Item::posterPath.name, posterPath ?: "")
                    put(Item::releaseDate.name, movie.releaseDate ?: "Unknown")
                    put(Item::rating.name, movie.rating)
                    put(Item::watched.name, false)
                }

                context.contentResolver.insert(
                    MOVIE_PROVIDER_CONTENT_URI,
                    values
                )
                insertedCount++
            }
            
            // Show notification with PendingIntent
            NotificationHelper.showMoviesFetchedNotification(context, insertedCount)
            
            // Notify that data import is complete
            context.sendBroadcast<MovieReciever>()
        }
    }
}