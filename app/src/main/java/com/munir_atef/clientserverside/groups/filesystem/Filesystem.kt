package com.munir_atef.clientserverside.groups.filesystem

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class Filesystem {
    fun info(filePath: String): JSONObject {
        val file = File(filePath)
        val isExists = file.exists()
        val fileInfo = JSONObject()
        fileInfo.put("isExists", isExists)

        if (!isExists) return fileInfo


        val type: String = if (file.isFile) "f" else if (file.isDirectory) "d" else "u"

        fileInfo.put("parentPath", file.parent)
        fileInfo.put("name", file.name)
        fileInfo.put("type", type)
        fileInfo.put("size", file.length())
        fileInfo.put("lastModified", file.lastModified())

        return fileInfo
    }

    fun delete(filePath: String, recursively: Boolean): Boolean {
        val file = File(filePath)
        val isExists = file.exists()
        if (isExists) {
            if (recursively) file.deleteRecursively()
            else file.delete()
        }
        return isExists
    }

    fun createFile(filePath: String, recursively: Boolean): Boolean {
        if (recursively) {
            val lastIndex: Int = filePath.lastIndexOf(File.pathSeparator)
            val parentFile = File(filePath.substring(0, lastIndex))

            if (!parentFile.exists())
                if (parentFile.mkdirs()) return File(filePath).createNewFile()
        } else return File(filePath).createNewFile()

        return false
    }

    fun createDir(dirPath: String, recursively: Boolean): Boolean {
        val file = File(dirPath)
        if (recursively) return file.mkdirs()
        return file.mkdir()
    }

    fun listDirContent(dirPath: String): JSONArray? {
        val rootFile = File(dirPath)
        if (!rootFile.isDirectory) return null

        val listedFiles: Array<File> = rootFile.listFiles() ?: return null
        val filesJson = JSONArray()

        listedFiles.forEach {
            filesJson.put(info(it.path))
        }

        return filesJson
    }

    fun copyFile(srcPath: String, destPath: String, overwrite: Boolean, recursively: Boolean): Boolean {
        val srcFile = File(srcPath)
        if (!srcFile.exists()) return false

        if (recursively) srcFile.copyRecursively(File(destPath), overwrite = overwrite)
        else srcFile.copyTo(File(destPath), overwrite)

        return true
    }

    fun readAsString(filePath: String): String? {
        val file = File(filePath)
        if (!file.exists()) return null

        return file.readText()
    }

    fun readAsBytes(filePath: String): ByteArray? {
        val file = File(filePath)
        if (!file.exists()) return null

        return file.readBytes()
    }

    fun writeString(filePath: String, content: String, overwrite: Boolean): Boolean {
        val file = File(filePath)
        if (!file.exists()) return false

        if (overwrite) file.writeText(content)
        else file.appendText(content)
        return true
    }

    fun writeBytes(filePath: String, content: ByteArray, overwrite: Boolean): Boolean {
        val file = File(filePath)
        if (!file.exists()) return false

        if (overwrite) file.writeBytes(content)
        else file.appendBytes(content)
        return true
    }
}

