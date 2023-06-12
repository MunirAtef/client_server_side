package com.munir_atef.clientserverside.local_server

import java.io.BufferedReader


class Request(inputStream: BufferedReader) {
    var url: String? = null
    var contentLength: Int? = null
    var body: String? = null
    var isService: Boolean = false
    var isFile: Boolean = false

    init {
        val headerLines: ArrayList<String> = arrayListOf()
        var line: String = inputStream.readLine()

        val parts = line.split(" ")
        val method: String = parts[0]
        url = parts[1]

        if (method == "POST" && url?.startsWith("/service") == true) isService = true
        else if (method == "GET" && url?.startsWith("/src") == true) isFile = true

        while (line != "") {
            headerLines.add(line)
            line = inputStream.readLine()
            if (line.startsWith("Content-Length:"))
                contentLength = line.split(" ")[1].toInt()
        }
        if (contentLength != null) {
            val array = CharArray(contentLength!!)
            inputStream.read(array)
            body = String(array)
        }
    }
}

