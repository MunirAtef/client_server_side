package com.munir_atef.clientserverside.groups.shared_pref

import com.munir_atef.clientserverside.groups.ResultTypes
import com.munir_atef.clientserverside.groups.ServiceResult
import com.munir_atef.clientserverside.groups.sqlite.LocalDatabase
import com.munir_atef.clientserverside.printf
import org.json.JSONArray
import org.json.JSONObject

/** CREATE TABLE kvt (id TEXT PRIMARY KEY, value TEXT, type INTEGER NOT NULL) */

class PreferencesInterface(private val db: LocalDatabase) {
    fun invoke(service: String, body: String): ServiceResult {
        printf(service)
        printf(body)

        val result: ServiceResult = when (service) {
            "get-item" -> getItem(body)
            "remove-item" -> removeItem(body)
            "get-all-items" -> getAllItems()
            "remove-all-items" -> removeAllItems()


            // @Deprecated
            "getString" -> getString(body)
            "getInt" -> getInt(body)
            "getDouble" -> getDouble(body)

            "setString" -> setString(body)
            "setInt" -> setInt(body)
            "setDouble" -> setDouble(body)

            "removeString" -> removeString(body)
            "removeInt" -> removeInt(body)
            "removeDouble" -> removeDouble(body)

            "getAllStrings" -> getAllStrings()
            "getAllIntegers" -> getAllIntegers()
            "getAllDoubles" -> getAllDoubles()

            "removeAll" -> removeAll()

            else -> ServiceResult(null, ResultTypes.ERROR_MESSAGE, false)
        }

        return result
    }

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

        return ServiceResult(json.toString(), ResultTypes.JSON, true)
    }

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

            when (queryResult.getJSONObject(i).optInt("value")) {
                0 -> json.put(key, value)
                1 -> json.put(key, value.toInt())
                2 -> json.put(key, value.toDouble())
            }
        }

        return ServiceResult(json.toString(), ResultTypes.JSON, true)
    }

    private fun removeAllItems(): ServiceResult {
        return ServiceResult(null, "", true)
    }


    /**
     * Expected body: key
     * */
    private fun getString(body: String): ServiceResult {
        val record: JSONArray = db.rawQuery("SELECT * FROM string WHERE id = ?", args = arrayOf(body))

        if (record.length() == 0) return ServiceResult(null, ResultTypes.EMPTY, true)

        val result: JSONObject = record.getJSONObject(0)

        return if (result.has("value"))
            ServiceResult(result.optString("value"), ResultTypes.TEXT, true)
        else
            ServiceResult(null, ResultTypes.EMPTY, true)
    }

    /**
     * Expected body: key
     * */
    private fun getInt(body: String): ServiceResult {
        val record: JSONArray = db.rawQuery("SELECT * FROM int WHERE id = ?", args = arrayOf(body))

        if (record.length() == 0) return ServiceResult(null, ResultTypes.EMPTY, true)

        val result: JSONObject = record.getJSONObject(0)

        return if (result.has("value"))
            ServiceResult(result.optString("value"), ResultTypes.TEXT, true)
        else
            ServiceResult(null, ResultTypes.EMPTY, true)
    }

    /**
     * Expected body: key
     * */
    private fun getDouble(body: String): ServiceResult {
        val record: JSONArray = db.rawQuery("SELECT * FROM double WHERE id = ?", args = arrayOf(body))

        if (record.length() == 0) return ServiceResult(null, ResultTypes.EMPTY, true)

        val result: JSONObject = record.getJSONObject(0)

        return if (result.has("value"))
            ServiceResult(result.optString("value"), ResultTypes.TEXT, true)
        else
            ServiceResult(null, ResultTypes.EMPTY, true)
    }


    /**
     * Expected body: {
     *     "key": pref_key,
     *     "value": str_value
     * }
     * */
    private fun setString(body: String): ServiceResult {
        val args = JSONObject(body)
        val key: String = args.optString("key")
        val value: String = args.optString("value")

        db.executeSQL("INSERT OR REPLACE INTO string VALUES (?, ?)", arrayOf(key, value))

        return ServiceResult(null, ResultTypes.EMPTY, true)
    }

    /**
     * Expected body: {
     *     "key": pref_key,
     *     "value": str_value
     * }
     * */
    private fun setInt(body: String): ServiceResult {
        val args = JSONObject(body)
        val key: String = args.optString("key")
        val value: Int = args.optInt("value")

        db.executeSQL("INSERT OR REPLACE INTO int VALUES (?, $value)", arrayOf(key))

        return ServiceResult(null, ResultTypes.EMPTY, true)
    }

    /**
     * Expected body: {
     *     "key": pref_key,
     *     "value": str_value
     * }
     * */
    private fun setDouble(body: String): ServiceResult {
        val args = JSONObject(body)
        val key: String = args.optString("key")
        val value: Double = args.optDouble("value")

        db.executeSQL("INSERT OR REPLACE INTO double VALUES (?, $value)", arrayOf(key))

        return ServiceResult(null, ResultTypes.EMPTY, true)
    }


    /**
     * Expected body: key
     * */
    private fun removeString(body: String): ServiceResult {
        db.executeSQL("DELETE FROM string WHERE id = ?", arrayOf(body))
        return ServiceResult(null, ResultTypes.EMPTY, true)
    }

    /**
     * Expected body: key
     * */
    private fun removeInt(body: String): ServiceResult {
        db.executeSQL("DELETE FROM int WHERE id = ?", arrayOf(body))
        return ServiceResult(null, ResultTypes.EMPTY, true)
    }

    /**
     * Expected body: key
     * */
    private fun removeDouble(body: String): ServiceResult {
        db.executeSQL("DELETE FROM double WHERE id = ?", arrayOf(body))
        return ServiceResult(null, ResultTypes.EMPTY, true)
    }


    private fun getAllStrings(): ServiceResult {
        val queryResult: JSONArray = db.rawQuery("SELECT * FROM string", null)
        val json = JSONObject()

        for (i: Int in 0 until queryResult.length()) {
            val key: String = queryResult.getJSONObject(i).optString("id")
            val value: String = queryResult.getJSONObject(i).optString("value")
            json.put(key, value)
        }

        return ServiceResult(json.toString(), ResultTypes.JSON, true)
    }

    private fun getAllIntegers(): ServiceResult {
        val queryResult: JSONArray = db.rawQuery("SELECT * FROM int", null)
        val json = JSONObject()

        for (i: Int in 0 until queryResult.length()) {
            val key: String = queryResult.getJSONObject(i).optString("id")
            val value: Int = queryResult.getJSONObject(i).optInt("value")
            json.put(key, value)
        }

        return ServiceResult(json.toString(), ResultTypes.JSON, true)
    }

    private fun getAllDoubles(): ServiceResult {
        val queryResult: JSONArray = db.rawQuery("SELECT * FROM double", null)
        val json = JSONObject()

        for (i: Int in 0 until queryResult.length()) {
            val key: String = queryResult.getJSONObject(i).optString("id")
            val value: Double = queryResult.getJSONObject(i).optDouble("value")
            json.put(key, value)
        }

        return ServiceResult(json.toString(), ResultTypes.JSON, true)
    }


    private fun removeAll(): ServiceResult {
        db.executeSQL("DELETE FROM string", null)
        db.executeSQL("DELETE FROM int", null)
        db.executeSQL("DELETE FROM double", null)

        return ServiceResult(null, ResultTypes.EMPTY, true)
    }
}

