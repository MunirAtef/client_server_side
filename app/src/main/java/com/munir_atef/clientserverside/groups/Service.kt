package com.munir_atef.clientserverside.groups

import android.content.Context
import com.munir_atef.clientserverside.groups.shared_pref.PreferencesInterface
import com.munir_atef.clientserverside.groups.sqlite.LocalDatabase
import com.munir_atef.clientserverside.groups.sqlite.SQLiteInterface
import com.munir_atef.clientserverside.manifest
import com.munir_atef.clientserverside.rootPath


class Service(private val context: Context) {
    private val databasePath: String = manifest.getDatabasePath()
    private val preferencesPath: String = manifest.getSharedPref()

    private var sqlite: SQLiteInterface? = null
    private var preferences: PreferencesInterface? = null

    fun invokeGroup(group: String, service: String, body: String): ServiceResult {
        return when (group) {
            "sqlite" -> {
                if (sqlite == null) sqlite = SQLiteInterface(LocalDatabase(
                    context,
                    databasePath.split("/").last(),
                    rootPath.value + "/assets" + databasePath
                ))
                sqlite!!.invoke(service, body)
            }
            "preferences" -> {
                if (preferences == null) preferences = PreferencesInterface(LocalDatabase(
                    context,
                    preferencesPath.split("/").last(),
                    rootPath.value + "/assets" + preferencesPath
                ))
                preferences!!.invoke(service, body)
            }

            else -> ServiceResult(null, ResultTypes.ERROR_MESSAGE, false)
        }
    }
}


class ServiceResult(val data: Any?, val type: String, val passed: Boolean)


object ResultTypes {
    const val TEXT: String = "text/plain"
    const val JSON: String = "application/json"
    const val EMPTY: String = "empty"
    const val ERROR_MESSAGE = "error"
}

