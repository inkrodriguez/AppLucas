package com.inkrodriguez.applucas

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE_QUERY = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_EMAIL TEXT PRIMARY KEY,"
                + "$COLUMN_PASSWORD TEXT,"
                + "$COLUMN_LASTPOSITION TEXT)"
                )
        db.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val DROP_TABLE_QUERY = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(DROP_TABLE_QUERY)
        onCreate(db)
    }

    fun insert(email: String, password: String, lastposition: Int) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)
        values.put(COLUMN_LASTPOSITION, lastposition)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun validate(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_EMAIL = '$email' AND $COLUMN_PASSWORD = '$password'"
        val cursor = db.rawQuery(query, null)
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "mydatabase.db"
        private const val TABLE_NAME = "users"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_LASTPOSITION = "lastposition"
    }
}
