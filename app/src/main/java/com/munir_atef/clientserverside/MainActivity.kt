package com.munir_atef.clientserverside

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.io.*
import java.util.*


fun printf(message: Any?) {
    Log.d("DEBUG", message.toString())
}

var rootPath: MutableState<String> = mutableStateOf("/storage/emulated/0/hybrid2/hybrid_file")



class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController: NavHostController = rememberNavController()

            NavHost(navController, startDestination = "main") {
                composable("main") { MainContent(navController) }
                composable("pick") { PickDir(navController) }
                composable("view") { Viewer(rootPath.value) }
            }
        }
    }
}



@ExperimentalPermissionsApi
@Composable
fun MainContent(navController: NavHostController) {
    val readPermissionState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    val writePermissionState =
        rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text("Hybrid Executable File", color = Color.White)
                },

                backgroundColor = Color(0xFF000000),
                actions = {
                    IconButton(
                        onClick = {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                            if (!readPermissionState.hasPermission)
                                readPermissionState.launchPermissionRequest()
                            if (!writePermissionState.hasPermission)
                                writePermissionState.launchPermissionRequest()
                        },
                        content = {
                            Text(
                                text = "RSP",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF)
                            )
                        }
                    )

                    IconButton(
                        onClick = {
                            navController.navigate("constrain")
                        },
                        content = {
                            Text(
                                text = "const",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF)
                            )
                        }
                    )
                }
            )
        },

        content = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,

                modifier = Modifier.fillMaxSize()
            ) {
                val context: Context = LocalContext.current

                Text(text = rootPath.value, fontSize = 20.sp, fontWeight = FontWeight.W500)

                Button(
                    onClick = {
                        navController.navigate("pick")
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .padding(5.dp)
                ) {
                    Text(text = "Pick Folder")
                }

                Button(
                    onClick = {
                        navController.navigate("view")
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .padding(5.dp)
                ) {
                    Text(text = "View Folder")
                }

                Button(
                    onClick = {
//                        val data =
//                            DatabaseHelper(context).readData("person", arrayOf("id", "name", "age"), "id > 0", null)
//
//                        printf(data)
//
//                        val data2 = DatabaseHelper(context).rawQuery("SELECT name, id FROM person WHERE id > 3", null)
//                        printf(data2)
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .padding(5.dp)
                ) {
                    Text(text = "Just For Test")
                }


                Button(
                    onClick = {
                        try {
                            val assetManager = context.assets
                            val inputStream: InputStream = assetManager.open("apis.html")
                            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))

                            val content: String = bufferedReader.readText()

                            File(rootPath.value + "/src/apis.html").writeText(content)
                        } catch (e: Exception) {
                            printf(e)
                        }
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .padding(5.dp)
                ) {
                    Text(text = "Update APIs Test File")
                }
            }
        }
    )
}



fun listDirs(path: String, sortByName: Boolean): Array<File>? {
    val rootFile = File(path)
    val listedFiles: Array<File>? = rootFile.listFiles{ pathname -> pathname.isDirectory }

    if (listedFiles != null && listedFiles.size > 1) {
        if (sortByName)
            Arrays.sort(listedFiles) {
                    object1, object2 -> object1.name.lowercase().compareTo(object2.name.lowercase())
            }
        else
            Arrays.sort(listedFiles) {
                    object1, object2 -> object2.lastModified().compareTo(object1.lastModified())
            }
    }

    return listedFiles
}

fun getPathFromList(listDirs: MutableList<String>): String {
    var path = "/storage/emulated/0"
    val size: Int = listDirs.size

    if (size > 1) {
        for (i: Int in 1 until size) {
            path += "/${listDirs[i]}"
        }
    }

    return path
}


@Composable
fun PickDir(navController: NavHostController) {
    val root = "/storage/emulated/0"
    val current = remember{ mutableStateOf(root) }
    var currentPath: MutableList<String> = remember { mutableStateListOf("Internal storage") }

    val sortByName = remember { mutableStateOf(true) }
//    val path = getPathFromList(currentPath)
//    Log.d("DEBUG", path)

    var files: Array<File>? = listDirs(getPathFromList(currentPath), sortByName.value)
    if (files == null) files = emptyArray()

    Log.d("DEBUG", files.size.toString())

    Scaffold(
        backgroundColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (current.value != root) {
                                        val backDir: String? = File(current.value).parent
                                        if (backDir != null) {
                                            current.value = backDir
                                        }
                                    }
                                },
                                content = {
                                    Icon(
                                        Icons.Filled.ArrowBack,
                                        tint = Color.White,
                                        contentDescription = "Back"
                                    )
                                }
                            )

                            Text("Pick Folder", color = Color.White)


                            IconButton(
                                onClick = {
                                    sortByName.value = !sortByName.value
                                },
                                content = {
                                    Icon(
                                        Icons.Filled.Sort,
                                        tint = Color.White,
                                        contentDescription = "Sort"
                                    )
                                }
                            )

                            IconButton(
                                onClick = {
                                    rootPath.value = current.value
                                    navController.popBackStack()
                                },
                                content = {
                                    Icon(
                                        Icons.Filled.Check,
                                        tint = Color.White,
                                        contentDescription = "Back"
                                    )
                                }
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState(), reverseScrolling = true)
                        ) {
                            for (i: Int in 0 until currentPath.size) {
                                Icon(
                                    Icons.Filled.ArrowForwardIos,
                                    contentDescription = "Arrow",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .height(15.dp)
                                        .width(15.dp)
                                )

                                TextButton(onClick = {
                                    try {
                                        val last = currentPath[i]
                                        currentPath = currentPath.subList(0, i - 1)
                                        Log.d("LAST", last)
                                        currentPath.add(last)
                                    } catch (e: Exception) {
                                        Log.wtf("WTF", e.message)
                                    }
                                }) {
                                    Text(text = currentPath[i])
                                }
                            }
                        }
                    }

                },

                backgroundColor = Color(0xFF000000),
                modifier = Modifier.height(80.dp)
            )
        },
    ) {
        LazyColumn(
            content = {
                items(files.size) { i ->
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                        contentPadding = PaddingValues(start = 10.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),

                        onClick = {
                            current.value += "/${files[i].name}"
                            currentPath.add(files[i].name)
                            Log.d("DEBUG", current.value)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Image(
                                painterResource(R.drawable.dir_icon),
                                contentDescription = "directory",
                                modifier = Modifier.width(40.dp)
                            )
                            Text(
                                text = files[i].name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W500,

                                modifier = Modifier
                                    .padding(start = 5.dp)
                                    .horizontalScroll(rememberScrollState())
                            )
                        }
                    }
                }
            }
        )
    }
}
