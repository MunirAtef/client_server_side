package com.munir_atef.clientserverside.groups.sqlite

import com.munir_atef.clientserverside.groups.ResultTypes
import com.munir_atef.clientserverside.groups.ServiceResult
import com.munir_atef.clientserverside.printf
import org.json.JSONArray
import org.json.JSONObject


class SQLiteInterface(private val db: LocalDatabase) {

    fun invoke(service: String, body: String): ServiceResult {
        printf(service)
        printf(body)

        val result: ServiceResult = when (service) {
            "insert" -> insertData(body)
            "read" -> readData(body)
            "update" -> update(body)
            "delete" -> delete(body)
            "query" -> rawQuery(body)
            "execute" -> executeSQL(body)

            else -> ServiceResult(null, ResultTypes.ERROR_MESSAGE, false)
        }

        return result
    }

    /**
     * Expected body: {
     *     "table": tableName,
     *     "data": {
     *         "column1": "value1",
     *         "column2": "value2",
     *         "column3": "value3"
     *     }
     * }
     * */
    private fun insertData(body: String): ServiceResult {
        val args = JSONObject(body)
        val tableName: String = args.optString("table")
        val data: JSONObject = args.optJSONObject("data")
            ?: return ServiceResult(null, ResultTypes.ERROR_MESSAGE, false)

        val result: Long =  db.insertData(tableName = tableName, data = data)
        return ServiceResult(result.toString().toByteArray(), ResultTypes.TEXT, true)
    }

    /**
     * Expected body: {
     *     "table": tableName,
     *     "columns": ["column1", "column2", "column3"],
     *     "where": "condition",
     *     "whereArgs": ["arg1", "arg2"]
     * }
     * */
    private fun readData(body: String): ServiceResult {
        printf(body)

        val args = JSONObject(body)

        if (!args.has("table")) return ServiceResult("error".toByteArray(), ResultTypes.ERROR_MESSAGE, false)

        val tableName: String = args.optString("table")
        val where: String = args.optString("where")
        val columns: Array<String>? = jsonArrayToArrayString(args.optJSONArray("columns"))
        val whereArgs: Array<String>? = jsonArrayToArrayString(args.optJSONArray("whereArgs"))

        val result: JSONArray = db.readData(
            tableName = tableName,
            columns = columns,
            condition = where,
            conditionArgs = whereArgs
        )

        return ServiceResult(result.toString().toByteArray(), ResultTypes.JSON, true)
    }

    /**
     * Expected body: {
     *     "table": tableName,
     *     "data": {
     *         "column1": "value1",
     *         "column2": "value2",
     *         "column3": "value3"
     *     }
     *     "where": "condition",
     *     "whereArgs": ["arg1", "arg2"]
     * }
     * */
    private fun update(body: String): ServiceResult {
        val args = JSONObject(body)

        val tableName: String = args.optString("table")
        val data: JSONObject = args.optJSONObject("data")
            ?: return ServiceResult(null, ResultTypes.ERROR_MESSAGE, false)

        val where: String? = if (args.has("where")) args.optString("where") else null
        val whereArgs: Array<String>? = jsonArrayToArrayString(args.optJSONArray("whereArgs"))

        val result: Int =  db.update(
            tableName = tableName,
            data = data,
            condition = where,
            conditionArgs = whereArgs
        )

        return ServiceResult(result.toString().toByteArray(), ResultTypes.TEXT, true)
    }

    /**
     * Expected body: {
     *     "table": tableName,
     *     "where": "condition",
     *     "whereArgs": ["arg1", "arg2"]
     * }
     * */
    private fun delete(body: String): ServiceResult {
        val args = JSONObject(body)

        val tableName: String = args.optString("table")
        val where: String? = if (args.has("where")) args.optString("where") else null
        val whereArgs: Array<String>? = jsonArrayToArrayString(args.optJSONArray("whereArgs"))

        val result: Int =  db.delete(
            tableName = tableName,
            condition = where,
            conditionArgs = whereArgs
        )

        return ServiceResult(result.toString().toByteArray(), ResultTypes.TEXT, true)
    }

    /**
     * Expected body: {
     *     "sql": sqlStatement,
     *     "args": ["arg1", "arg2"]
     * }
     * */
    private fun rawQuery(body: String): ServiceResult {
        val args = JSONObject(body)

        if (!args.has("sql")) return ServiceResult("error".toByteArray(), ResultTypes.ERROR_MESSAGE, false)

        val sql: String = args.optString("sql")
        val sqlArgs: Array<String>? = jsonArrayToArrayString(args.optJSONArray("args"))

        val result: JSONArray = db.rawQuery(
            sql = sql,
            args = sqlArgs
        )

        return ServiceResult(result.toString().toByteArray(), ResultTypes.JSON, true)
    }

    /**
     * Expected body: {
     *     "sql": sqlStatement,
     *     "args": ["arg1", "arg2"]
     * }
     * */
    private fun executeSQL(body: String): ServiceResult {
        val args = JSONObject(body)

        if (!args.has("sql")) return ServiceResult("error".toByteArray(), ResultTypes.ERROR_MESSAGE, false)

        val sql: String = args.optString("sql")
        val sqlArgs: Array<String>? = jsonArrayToArrayString(args.optJSONArray("args"))

        db.executeSQL(
            sql = sql,
            args = sqlArgs
        )

        return ServiceResult(null, ResultTypes.EMPTY, true)
    }



    private fun jsonArrayToArrayString(jsonArray: JSONArray?): Array<String>? {
        if (jsonArray == null) return null

        val list = mutableListOf<String>()

        for (i: Int in 0 until jsonArray.length()) {
            val value: String = jsonArray.getString(i)
            list.add(value)
        }

        return list.toTypedArray()
    }
}

