package com.munir_atef.clientserverside


import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.munir_atef.clientserverside.local_server.LocalServer


lateinit var manifest: ManifestModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Viewer(basePath: String) {
    printf(basePath)
    manifest = ManifestModel("$basePath/manifest.json")
    val launch: String = manifest.getLaunchFile()

    printf(launch)
    printf(manifest.getDatabasePath())
    printf(manifest.getSharedPref())
    printf(manifest.getPermissions())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Hybrid Executable File", color = Color.White)
                },
                backgroundColor = Color(0xFF000000),
                actions = {
                    Button(onClick = {
                        val rootThreadGroup = Thread.currentThread().threadGroup

                        if (rootThreadGroup != null) {
                            val threads = arrayOfNulls<Thread>(rootThreadGroup.activeCount())
                            rootThreadGroup.enumerate(threads)

                            // Filter out null values and print the names of the remaining threads
                            threads.filterNotNull().forEach { thread ->
                                printf("Thread: ${thread.id} : ${thread.name}")
                            }
                        }
                    }) {
                        Text(text = "threads")
                    }
                }
            )
        }
    ) {
//        ExecuteMessage.context = LocalContext.current

        printf("started")
        val server = LocalServer(8080, LocalContext.current, basePath)
        server.start()
        printf("opened")

        DisposableEffect(Unit) {
            onDispose {
                server.close()
            }
        }

        AndroidView(
            factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.apply {
                        javaScriptCanOpenWindowsAutomatically = true
                        allowContentAccess = true
                        allowFileAccess = true
                        javaScriptEnabled = true
                    }

                    webViewClient = WebViewClient()

                    loadUrl("http://localhost:8080/src/$launch")
                }
            },
        )
    }
}

