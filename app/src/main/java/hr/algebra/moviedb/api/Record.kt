package hr.algebra.moviedb.api

import com.google.gson.annotations.SerializedName

data class TmdbResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<MovieItem>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)
