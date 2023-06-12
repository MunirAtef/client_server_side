package com.munir_atef.clientserverside

import org.json.JSONObject
import java.io.File

class ManifestModel(manifestPath: String) {
    private val databasePath: String
    private val launchFile: String
    private val sharedPref: String
    private var permissions: List<String>? = null

    init {
        val manifestText: String = File(manifestPath).readText()
        printf(manifestText)

        val manifestJson = JSONObject(manifestText)
        databasePath = manifestJson.optString("databasePath")
        launchFile = manifestJson.optString("launchPage")
        sharedPref = manifestJson.optString("sharedPrefPath")
        val permissionsArray = manifestJson.optJSONArray("permissions")

        if (permissionsArray != null) {
            permissions = (0 until permissionsArray.length()).map {
                permissionsArray.getString(it)
            }.toList()
        }
    }

    fun getDatabasePath(): String { return databasePath }
    fun getLaunchFile(): String { return launchFile }
    fun getSharedPref(): String { return sharedPref }
    fun getPermissions(): List<String>? { return permissions }
}
