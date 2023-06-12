package com.munir_atef.clientserverside.local_server

import android.content.Context
import com.munir_atef.clientserverside.groups.ResultTypes
import com.munir_atef.clientserverside.groups.Service
import com.munir_atef.clientserverside.groups.ServiceResult
import com.munir_atef.clientserverside.printf
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.*


class LocalServer(port: Int, context: Context, private val rootDirectory: String) {
    private var serverSocket: ServerSocket
    private val service = Service(context)

    init {
        printf("will start")
        try {
            serverSocket = ServerSocket(port)
        } catch (e: Exception) {
            printf("ERROR: ${e.message}")
            serverSocket = ServerSocket(port)
        }

        printf("end")
    }

    fun start() {
        Thread {
            while (!serverSocket.isClosed) {
                try {
                    val clientSocket: Socket = serverSocket.accept()
                    Thread { handleRequest(clientSocket) }.start()
                } catch (e: SocketException) {
                    printf(e.message)
                }
            }
        }.start()
    }

    fun close() {
        serverSocket.close()
    }


    private fun handleRequest(socket: Socket) {
        val inputStream: BufferedReader = socket.getInputStream().bufferedReader()
        val outputStream = BufferedOutputStream(socket.getOutputStream())

        val request = Request(inputStream)

        val body: String? = request.body
        val url = request.url

        printf("$url\n$body\n==================================================")

        var failed = true

        if (request.isService) {
            val pathToService: List<String>? = url?.split("/")

            printf(pathToService)

            if (pathToService != null) {
                val serviceResult: ServiceResult =
                    service.invokeGroup(pathToService[2], pathToService[3], body ?: "")

                printf(serviceResult.passed)
                printf(serviceResult.data.toString())
                printf(serviceResult.type)


                if (serviceResult.passed) {
                    outputStream.write("HTTP/1.1 200 OK\r\n".toByteArray())
                    val data: ByteArray? = serviceResult.data

                    if (serviceResult.type != ResultTypes.EMPTY && data != null) {
                        val response = "Content-Type: ${serviceResult.type}" +
                                "\r\nContent-Length: ${data.size}\r\n\r\n"

                        outputStream.write(response.toByteArray() + data)
                    }
                    failed = false
                }
            }
        } else if (request.isFile) {
            val filePath: String = rootDirectory + url
            printf(filePath)

            // Serve the requested file
            val file = File(filePath)
            if (file.isFile) {
                val mimeType = getMimeType(file)
                val fileLength = file.length().toInt()

                val response = "HTTP/1.1 200 OK\r\nContent-Type: $mimeType\r\nContent-Length: $fileLength\r\n\r\n"

                outputStream.write(response.toByteArray())

                val fileInputStream = FileInputStream(file)
                val buffer = ByteArray(1024)
                var bytesRead = fileInputStream.read(buffer)
                while (bytesRead != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    bytesRead = fileInputStream.read(buffer)
                }
                fileInputStream.close()
                failed = false
            }
        }

        if (failed)
            outputStream.write("HTTP/1.1 404 Not Found\r\n\n".toByteArray())

        outputStream.flush()
        outputStream.close()
        inputStream.close()
        socket.close()
    }


    private fun getMimeType(file: File): String {
        val extension = file.extension
        return when (extension.lowercase(Locale.getDefault())) {
            "html" -> "text/html"
            "css" -> "text/css"
            "js" -> "text/javascript"
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "gif" -> "image/gif"
            else -> "application/octet-stream"
        }
    }
}




/**
 * SQLite
 * SharedPreferences
 * Filesystem
 */

