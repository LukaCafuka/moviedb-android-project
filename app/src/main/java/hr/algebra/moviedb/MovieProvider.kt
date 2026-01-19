package hr.algebra.moviedb

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.core.net.toUri
import hr.algebra.moviedb.dao.Repository
import hr.algebra.moviedb.dao.getRepository
import hr.algebra.moviedb.model.Item

private const val AUTHORITY = "hr.algebra.moviedb.provider"
private const val PATH = "items"
val MOVIE_PROVIDER_CONTENT_URI: Uri = "content://$AUTHORITY/$PATH".toUri()

private const val ITEMS = 10
private const val ITEM_ID = 20

private val URI_MATCHER = with(UriMatcher(UriMatcher.NO_MATCH)){
    // "content://hr.algebra.moviedb.provider/items : 10
    addURI(AUTHORITY, PATH, ITEMS)
    //"content://hr.algebra.moviedb.provider/items/22 : 20
    addURI(AUTHORITY, "$PATH/#", ITEM_ID)
    this
}

class MovieProvider : ContentProvider() {

    private lateinit var repository: Repository

  // "content://hr.algebra.moviedb.provider/items  -> SVI ITEMS add
  // "content://hr.algebra.moviedb.provider/items/22  -> SINGLE ITEM delete, select, update
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when(URI_MATCHER.match(uri)) {
            ITEMS -> return repository.delete(selection, selectionArgs)
            ITEM_ID -> {
                val id = uri.lastPathSegment
                if(id != null) {
                    return repository.delete("${Item::_id.name}=?", arrayOf(id))
                }
            }
        }

        throw IllegalArgumentException("WRONG URI")
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = repository.insert(values)
        return ContentUris.withAppendedId(MOVIE_PROVIDER_CONTENT_URI, id)
    }

    override fun onCreate(): Boolean {
        repository = getRepository(context)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor = repository.query(
        projection,
        selection,
        selectionArgs,
        sortOrder
    )

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        when(URI_MATCHER.match(uri)) {
            ITEMS -> return repository.update(values, selection, selectionArgs)
            ITEM_ID -> {
                val id = uri.lastPathSegment
                if(id != null) {
                    return repository.update(values,"${Item::_id.name}=?", arrayOf(id))
                }
            }
        }
        throw IllegalArgumentException("Wrong URI")
    }
}