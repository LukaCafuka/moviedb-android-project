package hr.algebra.moviedb.api

import retrofit2.Call
import retrofit2.http.GET


const val API_URL = "https://api.jsonbin.io/v3/b/"

interface MovieApi {
    @GET("695a9fc0d0ea881f40541f8a")
    fun fetchItems() : Call<Record>
}