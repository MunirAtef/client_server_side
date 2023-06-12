package com.munir_atef.clientserverside.groups.sqlite


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


class LocalDatabase(context: Context, databaseName: String, private val databasePath: String):
    SQLiteOpenHelper(context, databaseName, null, 1) {

    private val database: SQLiteDatabase = writableDatabase

    override fun onCreate(db: SQLiteDatabase) {}

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun getWritableDatabase(): SQLiteDatabase {
        val databaseFile = File(databasePath)
        return SQLiteDatabase.openOrCreateDatabase(databaseFile, null)
    }

//    fun closeConnection() {
//        database.close()
//    }

    private fun cursorToJson(cursor: Cursor): JSONArray {
        val jsonArray = JSONArray()

        if (cursor.count == 0) return JSONArray()

        cursor.moveToNext()
        val columnCount: Int = cursor.columnCount
        val columnNames: List<String> = cursor.columnNames.asList()
        val columnTypes: MutableList<Int> = mutableListOf()

        val cursorMethods = listOf(
            null, Cursor::getLong, Cursor::getFloat, Cursor::getString, Cursor::getBlob
        )

        fun cursorToObject(cursor: Cursor): JSONObject {
            val jsonObject = JSONObject()
            for (i in 0 until columnCount)
                jsonObject.put(columnNames[i], cursorMethods[columnTypes[i]]?.invoke(cursor, i))

            return jsonObject
        }

        for (i in 0 until columnCount) columnTypes.add(cursor.getType(i))

        jsonArray.put(cursorToObject(cursor))
        while (cursor.moveToNext()) jsonArray.put(cursorToObject(cursor))
        cursor.close()

        return jsonArray
    }


    fun insertData(tableName: String, data: JSONObject): Long {
        val values = ContentValues()

        data.keys().forEach { key: String ->
            when (val value = data[key]) {
                is String? -> values.put(key, value)
                is Int? -> values.put(key, value)
                is Double? -> values.put(key, value)
                is Long? -> values.put(key, value)
            }
        }

        return database.insert(tableName, null, values)
    }

    fun readData(
        tableName: String,
        columns: Array<String>?,
        condition: String?,
        conditionArgs: Array<String>?
    ): JSONArray {
        val cursor: Cursor = database.query(
            tableName,
            columns,
            condition,
            conditionArgs,
            null, null, null
        )

        return cursorToJson(cursor)
    }

    fun rawQuery(sql: String, args: Array<String>?): JSONArray {
        val cursor: Cursor = database.rawQuery(sql, args)
        return cursorToJson(cursor)
    }

    fun delete(tableName: String, condition: String?, conditionArgs: Array<String>?): Int {
        return database.delete(tableName, condition, conditionArgs)
    }

    fun update(tableName: String, data: JSONObject, condition: String?, conditionArgs: Array<String>?): Int {
        val values = ContentValues()

        data.keys().forEach { key: String ->
            when (val value = data[key]) {
                is String? -> values.put(key, value)
                is Int? -> values.put(key, value)
                is Double? -> values.put(key, value)
                is Long? -> values.put(key, value)
            }
        }

        return database.update(tableName, values, condition, conditionArgs)
    }

    fun executeSQL(sql: String?, args: Array<String>?) {
        database.execSQL(sql, args)
    }
}


