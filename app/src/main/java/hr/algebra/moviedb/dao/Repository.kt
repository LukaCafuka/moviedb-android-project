package hr.algebra.moviedb.dao

import android.content.ContentValues
import android.database.Cursor

// selection city = ? and mayor = ?
// selectionArgs [0] = "Zagreb"
// selectionArgs [1] = "Banderas"
interface Repository {
    fun delete(selection: String?, selectionArgs: Array<String>?): Int

    fun update(
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int

    fun query(
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor

    fun insert(values: ContentValues?): Long


}