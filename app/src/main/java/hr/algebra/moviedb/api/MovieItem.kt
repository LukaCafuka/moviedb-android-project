package hr.algebra.moviedb.api

import com.google.gson.annotations.SerializedName

data class MovieItem(
    @SerializedName("date") val date : String,
    @SerializedName("explanation") val explanation : String,
    @SerializedName("hdurl") val hdurl : String,
    @SerializedName("media_type") val media_type : String,
    @SerializedName("service_version") val service_version : String,
    @SerializedName("title") val title : String,
    @SerializedName("url") val url : String
)