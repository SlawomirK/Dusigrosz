package com.kobylinski.dusigrosz.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.support.design.widget.TabLayout
import android.util.Log

import android.widget.Toast
import com.kobylinski.dusigrosz.database.TableInfo.TABLE_COLUMN_ID
import com.kobylinski.dusigrosz.model.Debeter


object TableInfo : BaseColumns {
    const val TABLE_NAME = "debtors1"
    const val TABLE_COLUMN_ID="id"
    const val TABLE_COLUMN_NAME = "name"
    const val TABLE_COLUMN_AMOUNT = "amount"
    const val TABLE_COLUMN_CONTACT = "contact"
}

object SqlBasicComand {
    const val SQL_CREATE_TABLE: String =
        "CREATE TABLE IF NOT EXISTS"+TableInfo.TABLE_NAME+
                " ( "+TableInfo.TABLE_COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TableInfo.TABLE_COLUMN_NAME+" TEXT NOT NULL, " +
                TableInfo.TABLE_COLUMN_AMOUNT+ " REAL, " +
                TableInfo.TABLE_COLUMN_CONTACT+ " TEXT NOT NULL)"

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS "+TableInfo.TABLE_NAME
}

class DatabaseHelper(val context: Context) :SQLiteOpenHelper(context, TableInfo.TABLE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SqlBasicComand.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SqlBasicComand.SQL_DELETE_TABLE)
        onCreate(db)
    }

    fun inserDebeter( debt: Debeter) {
        val db = this.writableDatabase
        var cv = ContentValues()

        cv.put(TableInfo.TABLE_COLUMN_NAME, debt.name)
        cv.put(TableInfo.TABLE_COLUMN_AMOUNT, debt.debt)
        cv.put(TableInfo.TABLE_COLUMN_CONTACT, debt.phone)

        var result = db?.insert(TableInfo.TABLE_NAME, null, cv)

        if (result == -1.toLong())
            Toast.makeText(context, "BłĄD DODANIA", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, "DODANO", Toast.LENGTH_SHORT).show()
        }

    fun getAllDebeters(): ArrayList<Debeter> {
            val listDebeter=ArrayList<Debeter>()
            val selectQuery="SELECT * FROM ${TableInfo.TABLE_NAME}"
            val db=this.writableDatabase
            val cursor=db.rawQuery(selectQuery,null)
            if(cursor.moveToFirst()){
                do{
                    val name=cursor.getString(cursor.getColumnIndex(TableInfo.TABLE_COLUMN_NAME))
                    val contact=cursor.getString(cursor.getColumnIndex(TableInfo.TABLE_COLUMN_CONTACT))
                    val sum=cursor.getDouble(cursor.getColumnIndex(TableInfo.TABLE_COLUMN_AMOUNT))
                    val deb=Debeter(name,contact,sum)
                    listDebeter.add(deb)
                }while (cursor.moveToNext())
            }

        return listDebeter
    }

    fun getDebeter(id: String): Cursor? {
        val db = this.writableDatabase
        val cur=db.rawQuery("SELECT * From ${TableInfo.TABLE_NAME} where ID=id", null)
        db.close()
        return cur
    }

    fun updateDebeterData(id: String, name: String, debt: Double, phone: String): Boolean? {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(TableInfo.TABLE_COLUMN_NAME, name)
        cv.put(TableInfo.TABLE_COLUMN_AMOUNT, debt)
        cv.put(TableInfo.TABLE_COLUMN_CONTACT, phone)
        db.update(TableInfo.TABLE_NAME, cv, "ID=?", arrayOf(id))
        db.close()
        return true
    }

    fun removeDebeter(id: String): Int? {
        val db = this.writableDatabase
        val remove=db.delete(TableInfo.TABLE_NAME, "id=?", arrayOf(id))
        db.close()
        return remove
    }

}