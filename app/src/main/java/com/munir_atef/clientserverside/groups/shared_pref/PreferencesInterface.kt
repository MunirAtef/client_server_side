package com.munir_atef.clientserverside.groups.shared_pref

import com.munir_atef.clientserverside.groups.ResultTypes
import com.munir_atef.clientserverside.groups.ServiceResult
import com.munir_atef.clientserverside.groups.sqlite.LocalDatabase
import org.json.JSONArray
import org.json.JSONObject

/** CREATE TABLE kvt (id TEXT PRIMARY KEY, value TEXT NOT NULL, type INTEGER NOT NULL) */

class PreferencesInterface(private val db: LocalDatabase) {
    fun invoke(service: String, body: String): ServiceResult {
        val result: ServiceResult = when (service) {
            "set-item" -> setItem(body)
            "get-item" -> getItem(body)
            "remove-item" -> removeItem(body)
            "get-all-items" -> getAllItems()
            "remove-all-items" -> removeAllItems()

            else -> ServiceResult(null, ResultTypes.ERROR_MESSAGE, false)
        }

        return result
    }


    /**
     * Expected body: {
     *     "key": key,
     *     "value": value
     * }
     * */
    private fun setItem(body: String): ServiceResult {
        val args = JSONObject(body)
        val key: String = args.optString("key")

        when (val value: Any? = args.opt("value")) {
            is String -> db.executeSQL("INSERT OR REPLACE INTO kvt VALUES (?, ?, 0)", arrayOf(key, value))
            is Int -> db.executeSQL("INSERT OR REPLACE INTO kvt VALUES (?, ?, 1)", arrayOf(key, value.toString()))
            is Double -> db.executeSQL("INSERT OR REPLACE INTO kvt VALUES (?, ?, 2)", arrayOf(key, value.toString()))
            else -> return ServiceResult(null, ResultTypes.ERROR_MESSAGE, false)
        }

        return ServiceResult(null, ResultTypes.EMPTY, true)
    }

    /**
     * Expected body: key
     * */
    private fun getItem(body: String): ServiceResult {
        val record: JSONArray = db.rawQuery("SELECT * FROM kvt WHERE id = ?", args = arrayOf(body))

        if (record.length() == 0) return ServiceResult(null, ResultTypes.EMPTY, true)

        val result: JSONObject = record.getJSONObject(0)

        val key: String = result.optString("id")
        val value: String = result.optString("value")
        val type: Int = result.optInt("type")

        val json = JSONObject()

        when (type) {
            0 -> json.put(key, value)
            1 -> json.put(key, value.toInt())
            2 -> json.put(key, value.toDouble())
        }

        return ServiceResult(json.toString().toByteArray(), ResultTypes.JSON, true)
    }

    /**
     * Expected body: key
     * */
    private fun removeItem(body: String): ServiceResult {
        db.executeSQL("DELETE FROM kvt WHERE id = ?", arrayOf(body))
        return ServiceResult(null, ResultTypes.EMPTY, true)
    }

    private fun getAllItems(): ServiceResult {
        val queryResult: JSONArray = db.rawQuery("SELECT * FROM kvt", null)
        val json = JSONObject()

        for (i: Int in 0 until queryResult.length()) {
            val key: String = queryResult.getJSONObject(i).optString("id")
            val value: String = queryResult.getJSONObject(i).optString("value")

            when (queryResult.getJSONObject(i).optInt("type")) {
                0 -> json.put(key, value)
                1 -> json.put(key, value.toInt())
                2 -> json.put(key, value.toDouble())
            }
        }

        return ServiceResult(json.toString().toByteArray(), ResultTypes.JSON, true)
    }

    private fun removeAllItems(): ServiceResult {
        db.executeSQL("DELETE FROM kvt", arrayOf())
        return ServiceResult(null, ResultTypes.EMPTY, true)
    }
}

