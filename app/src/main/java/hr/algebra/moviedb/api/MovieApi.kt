package hr.algebra.moviedb.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

const val API_URL = "https://api.themoviedb.org/3/"
const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
// TODO: Replace with your TMDB API key from https://www.themoviedb.org/settings/api
const val API_KEY = "22a2d99dc7952ace56ae2edea7d8607a"

interface MovieApi {
    @GET("movie/popular")
    fun fetchPopularMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int = 1
    ): Call<TmdbResponse>
    
    @GET("movie/top_rated")
    fun fetchTopRatedMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int = 1
    ): Call<TmdbResponse>
    
    @GET("movie/now_playing")
    fun fetchNowPlayingMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int = 1
    ): Call<TmdbResponse>
}