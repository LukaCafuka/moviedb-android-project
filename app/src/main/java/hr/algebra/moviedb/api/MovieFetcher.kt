package hr.algebra.moviedb.api

import android.content.ContentValues
import android.content.Context
import android.util.Log
import hr.algebra.moviedb.MOVIE_PROVIDER_CONTENT_URI
import hr.algebra.moviedb.MovieReciever
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
import retrofit2.create

class MovieFetcher(private val context: Context) {

    private val movieApi: MovieApi
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        movieApi = retrofit.create<MovieApi>()
    }

    fun fetchItems(count: Int = 10) {
        val request = movieApi.fetchItems()

        request.enqueue(object: Callback<Record> {
            override fun onResponse(
                call: Call<Record?>,
                response: Response<Record?>
            ) {
                response.body()?.record.let { populateItems(it) }
            }

            override fun onFailure(
                call: Call<Record?>,
                t: Throwable
            ) {
                Log.e("ERROR", t.toString(), t)
            }
        })

    }

    private fun populateItems(movieItems: List<MovieItem>?) {

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            movieItems?.forEach {
                val picturePath = download(context, it.url)

                val values = ContentValues().apply {
                    put(Item::title.name, it.title)
                    put(Item::explanation.name, it.explanation)
                    put(Item::picturePath.name, picturePath ?: "")
                    put(Item::date.name, it.date)
                    put(Item::read.name, false)
                }



                context.contentResolver.insert(
                    MOVIE_PROVIDER_CONTENT_URI,
                    values
                )

            }
            // back to the FG
            context.sendBroadcast<MovieReciever>()
        }
    }

}