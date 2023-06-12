package com.munir_atef.clientserverside.groups.filesystem

import com.munir_atef.clientserverside.groups.ResultTypes
import com.munir_atef.clientserverside.groups.ServiceResult
import org.json.JSONArray
import org.json.JSONObject


class FilesystemInterface {
    private val filesystem = Filesystem()

    fun invoke(service: String, body: String): ServiceResult {
        val result: ServiceResult = when (service) {
            "info" -> info(body)
            "delete" -> delete(body)
            "create-file" -> createFile(body)
            "create-dir" -> createDir(body)
            "list-content" -> listDirContent(body)
            "copy-file" -> copyFile(body)
            "read-string" -> readAsString(body)
            "read-bytes" -> readAsBytes(body)
            "write-string" -> writeString(body)
            "write-bytes" -> writeBytes(body)

            else -> ServiceResult(null, ResultTypes.ERROR_MESSAGE, false)
        }

        return result
    }


    /** Expected body: filePath */
    private fun info(body: String): ServiceResult {
        val fileInfo: JSONObject = filesystem.info(body)
        return ServiceResult(fileInfo.toString().toByteArray(), ResultTypes.JSON, true)
    }

    /**
     * Expected body: {
     *     "path": String,
     *     "recursively": Boolean?
     * }
     * */
    private fun delete(body: String): ServiceResult {
        val args = JSONObject(body)
        val path: String = args.optString("path")
        val recursively: Boolean = args.optBoolean("recursively")

        val result: Boolean = filesystem.delete(path, recursively)

        return ServiceResult(null, ResultTypes.EMPTY, result)
    }

    /**
     * Expected body: {
     *     "path": String,
     *     "recursively": Boolean?
     * }
     * */
    private fun createFile(body: String): ServiceResult {
        val args = JSONObject(body)
        val path: String = args.optString("path")
        val recursively: Boolean = args.optBoolean("recursively")

        val result: Boolean = filesystem.createFile(path, recursively)
        return ServiceResult(null, ResultTypes.EMPTY, result)
    }

    /**
     * Expected body: {
     *     "path": String,
     *     "recursively": Boolean?
     * }
     * */
    private fun createDir(body: String): ServiceResult {
        val args = JSONObject(body)
        val path: String = args.optString("path")
        val recursively: Boolean = args.optBoolean("recursively")

        val result: Boolean = filesystem.createDir(path, recursively)
        return ServiceResult(null, ResultTypes.EMPTY, result)
    }

    /** Expected body: filePath */
    private fun listDirContent(body: String): ServiceResult {
        val listedContent: JSONArray = filesystem.listDirContent(body)
            ?: return ServiceResult(null, ResultTypes.ERROR_MESSAGE, false)
        return ServiceResult(listedContent.toString().toByteArray(), ResultTypes.JSON, true)
    }

    /**
     * Expected body: {
     *     "src": String,
     *     "dest": String,
     *     "overwrite": Boolean?,
     *     "recursively": Boolean?
     * }
     * */
    private fun copyFile(body: String): ServiceResult {
        val args = JSONObject(body)

        val src: String = args.optString("src")
        val dest: String = args.optString("dest")
        val overwrite: Boolean = args.optBoolean("overwrite")
        val recursively: Boolean = args.optBoolean("recursively")

        val result: Boolean = filesystem.copyFile(src, dest, overwrite, recursively)
        return ServiceResult(null, ResultTypes.EMPTY, result)
    }

    /** Expected body: filePath */
    private fun readAsString(body: String): ServiceResult {
        val content: String = filesystem.readAsString(body)
            ?: return ServiceResult(null, ResultTypes.ERROR_MESSAGE, false)
        return ServiceResult(content.toByteArray(), ResultTypes.TEXT, true)
    }

    /** Expected body: filePath */
    private fun readAsBytes(body: String): ServiceResult {
        val content: ByteArray = filesystem.readAsBytes(body)
            ?: return ServiceResult(null, ResultTypes.ERROR_MESSAGE, false)

        return ServiceResult(content, ResultTypes.OCTET_STREAM, true)
    }

    /**
     * Expected body: {
     *     "path": String,
     *     "content": String,
     *     "overwrite": Boolean?
     * }
     * */
    private fun writeString(body: String): ServiceResult {
        val args = JSONObject(body)
        val path: String = args.optString("path")
        val content: String = args.optString("content")
        val overwrite: Boolean = args.optBoolean("overwrite")

        val result: Boolean = filesystem.writeString(path, content, overwrite)
        return ServiceResult(null, ResultTypes.EMPTY, result)
    }

    /**
     * Expected body: {
     *     "path": String,
     *     "content": JSONArray<Int>,
     *     "overwrite": Boolean?
     * }
     * */
    private fun writeBytes(body: String): ServiceResult {
        val args = JSONObject(body)
        val path: String = args.optString("path")
        val content: JSONArray = args.optJSONArray("content") ?: JSONArray()
        val overwrite: Boolean = args.optBoolean("overwrite")

        val contentLength: Int = content.length()
        val contentBytes = ByteArray(contentLength)
        for (i: Int in 0 until contentLength)
            contentBytes[i] = content.getInt(i).toByte()

        val result: Boolean = filesystem.writeBytes(path, contentBytes, overwrite)
        return ServiceResult(null, ResultTypes.EMPTY, result)
    }
}



