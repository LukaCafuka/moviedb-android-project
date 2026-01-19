package hr.algebra.moviedb.dao

import android.content.Context

fun getRepository(context: Context?) = DBRepository(context)