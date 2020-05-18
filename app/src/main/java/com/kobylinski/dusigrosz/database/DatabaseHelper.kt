package com.kobylinski.dusigrosz.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

import com.kobylinski.dusigrosz.model.Debeter


object TableInfo : BaseColumns {
    const val TABLE_NAME = "debtors1"
    const val TABLE_COLUMN_ID = "id"
    const val TABLE_COLUMN_NAME = "name"
    const val TABLE_COLUMN_AMOUNT = "amount"
    const val TABLE_COLUMN_CONTACT = "contact"
}

object SqlBasicComand {
    const val SQL_CREATE_TABLE: String =
        "CREATE TABLE IF NOT EXISTS  ${TableInfo.TABLE_NAME} (" +
                "${TableInfo.TABLE_COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${TableInfo.TABLE_COLUMN_NAME} TEXT NOT NULL, " +
                "${TableInfo.TABLE_COLUMN_AMOUNT} REAL, " +
                "${TableInfo.TABLE_COLUMN_CONTACT} TEXT NOT NULL);"

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TableInfo.TABLE_NAME
}


class DatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, TableInfo.TABLE_NAME, null, 1) {

    companion object MyDataBase {
        var INSTANCE: DatabaseHelper? = null

        fun openDatabase(context: Context): DatabaseHelper {
            return INSTANCE ?: DatabaseHelper(context)
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SqlBasicComand.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SqlBasicComand.SQL_DELETE_TABLE)
        onCreate(db)
    }

    fun inserDebeter(debt: Debeter): Long? {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(TableInfo.TABLE_COLUMN_NAME, debt.name)
        cv.put(TableInfo.TABLE_COLUMN_AMOUNT, debt.debt)
        cv.put(TableInfo.TABLE_COLUMN_CONTACT, debt.phone)

        var result = db?.insert(TableInfo.TABLE_NAME, null, cv)
        db.close()
        return result
    }

    fun getAllDebeters(): ArrayList<Debeter> {
        val listDebeter = ArrayList<Debeter>()
        getCursorAllDebts(listDebeter)
        return listDebeter
    }

    fun getCursorAllDebts(listDebeter: ArrayList<Debeter>): Cursor? {
        val selectQuery = "SELECT * FROM ${TableInfo.TABLE_NAME}"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(TableInfo.TABLE_COLUMN_NAME))
                val contact =
                    cursor.getString(cursor.getColumnIndex(TableInfo.TABLE_COLUMN_CONTACT))
                val sum = cursor.getDouble(cursor.getColumnIndex(TableInfo.TABLE_COLUMN_AMOUNT))
                val deb = Debeter(name, contact, sum)
                listDebeter.add(deb)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return cursor
    }

    fun getDebeter(id: String): Cursor? {
        val db = this.writableDatabase
        val cur = db.rawQuery(
            "SELECT * From ${TableInfo.TABLE_NAME} where ${TableInfo.TABLE_COLUMN_ID}=$id",
            null
        )
        cur.close()
        db.close()
        return cur
    }

    fun getId(debt: Debeter): Long {
        val name = debt.name
        val phone = debt.phone
        val db = this.writableDatabase
        val cur = db.rawQuery(
            "SELECT ${TableInfo.TABLE_COLUMN_ID} FROM ${TableInfo.TABLE_NAME} WHERE ${TableInfo.TABLE_COLUMN_NAME} = ? AND ${TableInfo.TABLE_COLUMN_CONTACT} = ?",
            arrayOf(name, phone)
        )
        var id: Long = -1
        if (cur.moveToFirst()) {
            while (cur.isAfterLast == false) {
                id = cur.getInt(cur.getColumnIndex(TableInfo.TABLE_COLUMN_ID)).toLong()
                cur.moveToNext()
            }
        }
        return id
    }

    fun updateDebeterData(
        name: String,
        debt: Double,
        phone: String,
        id: Long
    ): Boolean? {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(TableInfo.TABLE_COLUMN_NAME, name)
        cv.put(TableInfo.TABLE_COLUMN_AMOUNT, debt)
        cv.put(TableInfo.TABLE_COLUMN_CONTACT, phone)
        val whereClasule = TableInfo.TABLE_COLUMN_ID + "=?"
        db.update(TableInfo.TABLE_NAME, cv, whereClasule, arrayOf(id.toString()))
        db.close()
        return true
    }

    fun removeDebeter(id: String): Int? {
        val db = this.writableDatabase
        val remove = db.delete(TableInfo.TABLE_NAME, "id=?", arrayOf(id))
        db.close()
        return remove
    }
}


