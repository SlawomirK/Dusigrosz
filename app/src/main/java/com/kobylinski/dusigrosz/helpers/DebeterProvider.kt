package com.kobylinski.dusigrosz.helpers

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.kobylinski.dusigrosz.database.DatabaseHelper
import com.kobylinski.dusigrosz.model.Debeter

class DebeterProvider : ContentProvider() {

    val db by lazy { context?.let { DatabaseHelper.MyDataBase.openDatabase(it) } }

    private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI("com.kobylinski.dusigrosz", "debtors1", 1)
        addURI("com.kobylinski.dusigrosz", "debtors1/#", 2)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw NotImplementedError()
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = context?.applicationContext?.let {
            DatabaseHelper.MyDataBase.openDatabase(it)
        } ?: throw RuntimeException("Brak kontekstu")
        var list = ArrayList<Debeter>()
        return when (sUriMatcher.match(uri)) {
            1 -> db.getCursorAllDebts(list)
            2 -> {
                val id = ContentUris.parseId(uri)
                db.getDebeter(id.toString())
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw NotImplementedError()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw NotImplementedError()
    }

    override fun getType(uri: Uri): String? =
        when (sUriMatcher.match(uri)) {
            1 -> "vnd.android.cursor.dir/vnd.com.kobylinski.dusigrosz.debeter"
            2 -> "vnd.android.cursor.item/vnd.com.kobylinski.dusigrosz.debeter"
            else -> throw IllegalArgumentException()

        }

}

