package hr.algebra.moviedb.model

import java.io.Serializable

data class Item(
    var _id: Long?,
    val title: String,
    val overview: String,
    val posterPath: String,
    val releaseDate: String,
    val rating: Double,
    var watched: Boolean
) : Serializable
