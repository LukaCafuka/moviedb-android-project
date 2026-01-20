package hr.algebra.moviedb.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import hr.algebra.moviedb.model.Item

private const val DB_NAME = "movies.db"
private const val DB_VERSION = 2
private const val TABLE_NAME = "movies"
private val CREATE_TABLE = "create table $TABLE_NAME( " +
        "${Item::_id.name} integer primary key autoincrement, " +
        "${Item::title.name} text not null, " +
        "${Item::overview.name} text not null, " +
        "${Item::posterPath.name} text not null, " +
        "${Item::releaseDate.name} text not null, " +
        "${Item::rating.name} real not null, " +
        "${Item::watched.name} integer not null" +
        ")"
private const val DROP_TABLE = "drop table if exists $TABLE_NAME"

class DBRepository(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION)
    ,Repository {
    override fun delete(
        selection: String?,
        selectionArgs: Array<String>?
    ) = writableDatabase.delete(
        TABLE_NAME,
        selection,
        selectionArgs
    )

    override fun update(
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ) = writableDatabase.update(
        TABLE_NAME,
        values,
        selection,
        selectionArgs
    )

    override fun query(
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor = readableDatabase.query(
        TABLE_NAME,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        sortOrder
    )

    override fun insert(values: ContentValues?)
        = writableDatabase.insert(
            TABLE_NAME,
            null,
            values
        )


    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL(CREATE_TABLE)

    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Drop old tables from previous version
        db?.execSQL("drop table if exists items")
        db?.execSQL(DROP_TABLE)
        onCreate(db)
    }
}