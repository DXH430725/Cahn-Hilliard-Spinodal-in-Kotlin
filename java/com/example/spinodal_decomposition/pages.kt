package com.example.spinodal_decomposition




import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spinodal_decomposition.ui.theme.Spinodal_decompositionTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Composable
fun HomePage(context: Context,onNavigateToParaPage: () -> Unit,) {
    // 跟踪对话框的显示状态
    val showDialog = remember { mutableStateOf(false) }

    Scaffold(

        topBar = {
            val painter: Painter = painterResource(id = R.drawable.dxh_logo)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Image(
                        painter = painter,
                        contentDescription = "logo",
                        modifier = Modifier
                            .size(200.dp)
                    )
                }

            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick ={ showDialog.value = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
        }

    ) {innerPadding ->
        // 根据showDialog的值决定是否显示对话框
        if (showDialog.value) {
            // 调用自定义的AlertDialog
            CustomAlertDialog(showDialog = showDialog)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Text(text = "Spinodal",fontSize = 48.sp,fontWeight = FontWeight.Bold)
            }
            Row {
                Text(text = "Decomposition",fontSize = 48.sp,fontWeight = FontWeight.Bold)
            }
            Row {
                Text(text = "Simulation",fontSize = 36.sp,fontWeight = FontWeight.Bold)
            }
            Row {
                Button(
                    onClick = onNavigateToParaPage,
                    modifier = Modifier
                        .padding(16.dp) // 设置按钮的内边距
                        .padding(horizontal = 32.dp, vertical = 16.dp) // 设置水平和垂直方向上的内边距
                        .size(width = 200.dp, height = 64.dp), // 设置按钮的宽度和高度
                    shape = RoundedCornerShape(16.dp) // 设置按钮的圆角尺寸
                ) {
                    Text("Start",fontSize = 24.sp)
                }
            }
        }
    }
}
@Composable
fun CustomAlertDialog(showDialog: MutableState<Boolean>) {
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text("ABOUT") },
        text = { Text("Made by DuXiaohan(2021141520019)") },
        confirmButton = {
            TextButton(
                onClick = { showDialog.value = false }
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { showDialog.value = false }
            ) {
                Text("取消")
            }
        }
    )
}

@Composable
fun ParaPage(context: Context,simulationParameters: SimulationParameters,onNavigateToStartPage: () -> Unit) {
    val showDialog = remember { mutableStateOf(false) }
    // 准备状态变量以在Compose中使用
    val (nx, setNx) = remember { mutableStateOf(simulationParameters.nx.toString()) }
    val (ny, setNy) = remember { mutableStateOf(simulationParameters.ny.toString()) }
    val (dx, setDx) = remember { mutableStateOf(simulationParameters.dx.toString()) }
    val (dy, setDy) = remember { mutableStateOf(simulationParameters.dy.toString()) }
    val (c0, setC0) = remember { mutableStateOf(simulationParameters.c0.toString()) }
    val (temp, setTemp) = remember { mutableStateOf(simulationParameters.temp.toString()) }
    val (nsteps, setNsteps) = remember { mutableStateOf(simulationParameters.nsteps.toString()) }

    Column(modifier = Modifier
        .background(color = MaterialTheme.colorScheme.background)
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Card(Modifier.padding(16.dp)) {
            TextField(
                value = nx,
                onValueChange = setNx,
                label = { Text("nx") },
                modifier = Modifier.fillMaxWidth(),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = ny,
                onValueChange = setNy,
                label = { Text("ny") }
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = dx,
                onValueChange = setDx,
                label = { Text("dx (m)") }
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = dy,
                onValueChange = setDy,
                label = { Text("dy (m)") }
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = c0,
                onValueChange = setC0,
                label = { Text("c0 (atomic fraction)") }
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = temp,
                onValueChange = setTemp,
                label = { Text("Temperature (K)") }
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = nsteps,
                onValueChange = setNsteps,
                label = { Text("Number of time-steps") }
            )
        }

        Button(onClick = { showDialog.value = true }) {
            Text("Update Parameters")
        }
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Warning") },
                text = { Text("It is not recommended to set parameters yourself unless you know what you are doing") },
                confirmButton = {
                    Button(onClick = {
                        showDialog.value = false
                        // 在这里调用参数更新逻辑
                        simulationParameters.updateSimulationParameters(
                            newNx = nx.toInt(),
                            newNy = ny.toInt(),
                            newDx = dx.toDouble(),
                            newDy = dy.toDouble(),
                            newC0 = c0.toDouble(),
                            newTemp = temp.toInt(),
                            newNsteps = nsteps.toInt()
                        )
                        println("Updated Parameters:")
                        println("nx: $nx, ny: $ny, dx: $dx, dy: $dy, c0: $c0, temp: $temp, nsteps: $nsteps")
                    }) {
                        Text("Continue")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        Toast.makeText(context,"change has not save",Toast.LENGTH_SHORT).show()
                        showDialog.value = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Button(onClick = onNavigateToStartPage) {
            Text("Go to render")
        }
    }
}


class ProgressViewModel : ViewModel() {
    val progress = mutableStateOf(0f) // 进度初始化为0

    fun updateProgress(value: Float) {
        progress.value = value
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartPage(context: Context, onNavigateToFinalPage: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    // 状态管理：是否正在执行任务
    val isProcessing = remember { mutableStateOf(false) }
    val progressViewModel = viewModel<ProgressViewModel>() // 创建ViewModel

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF50727B),
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        modifier = Modifier
                            .padding(4.dp) // 设置按钮的内边距
                            .size(width = 256.dp, height = 64.dp), // 设置按钮的宽度和高度
                        shape = RoundedCornerShape(5.dp), // 设置按钮的圆角尺寸
                        onClick = { onNavigateToFinalPage() },
                        enabled = !isProcessing.value
                    ) {
                        Text("View Result",fontSize = 20.sp)
                    }
                }

            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally)
        {
        Row(modifier = Modifier.padding(top = 32.dp, bottom = 32.dp)) {
            // 使用remember记住initial_frame的结果，这样它只会在首次加载时被调用
            val bitmap = remember { initial_frame().asImageBitmap() }

            Image(
                bitmap = bitmap,
                contentDescription = "Simulated Image",
                modifier = Modifier.size(300.dp)
            )
        }

        // 加载条
        if (isProcessing.value) {
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp) // 直接在参数中设置大小
            )
        }
        // 显示进度条和百分比
        if (isProcessing.value) {
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(progress = progressViewModel.progress.value)
            Text(text = "Rendering....",fontSize = 18.sp)
            Text("${(progressViewModel.progress.value * 100).toInt()}%",fontSize = 18.sp)
        }

        Row() {
            Button(
                onClick = {
                    isProcessing.value = true
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            circulation(context, progressViewModel)
                            encodeGifFromSavedImages(context)
                            deleteAllFilesInDirectory(context)
                        }
                        isProcessing.value = false
                    }
                },
                enabled = !isProcessing.value,
                modifier = Modifier
                    .padding(4.dp) // 设置按钮的内边距
                    .size(width = 128.dp, height = 64.dp), // 设置按钮的宽度和高度
                shape = RoundedCornerShape(16.dp), // 设置按钮的圆角尺寸
            ) {
                if (isProcessing.value) {
                    Text("Processing...",fontSize = 12.sp)
                } else {
                    Text("Start",fontSize = 24.sp)
                }
            }
        }

    }
    }
}


@Composable
fun FinalPage(context: Context,onNavigateToHomePage: () -> Unit) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                gifFilePath?.let { saveGifToGallery(context, it) }
            }) {
                Icon(Icons.Filled.Share, contentDescription = "Save")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(Modifier.size(360.dp)) {
                GifPlayer(gifFilePath = gifFilePath)
            }
            Row {
                Button(
                    onClick = { onNavigateToHomePage() },
                    modifier = Modifier
                        .padding(4.dp) // 设置按钮的内边距
                        .size(width = 128.dp, height = 64.dp), // 设置按钮的宽度和高度
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Home", fontSize = 24.sp)
                }
            }
        }
    }
}

fun saveGifToGallery(context: Context, gifFilePath: String) {
    // 假设gifFilePath是你的GIF文件的绝对路径
    val gifFile = File(gifFilePath)
    try {
        // 使用MediaStore将文件保存到系统相册
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, gifFile.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/gif")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val item = context.contentResolver.insert(collection, values)

        context.contentResolver.openFileDescriptor(item!!, "w", null).use { pfd ->
            FileOutputStream(pfd!!.fileDescriptor).use { outStream ->
                FileInputStream(gifFile).use { inputStream ->
                    val buf = ByteArray(8192)
                    while (true) {
                        val sz = inputStream.read(buf)
                        if (sz <= 0) break
                        outStream.write(buf, 0, sz)
                    }
                }
            }
        }

        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        context.contentResolver.update(item, values, null, null)

        // 显示成功信息
        Toast.makeText(context,"Save Success",Toast.LENGTH_SHORT).show()
        Log.d("GifCreation", "GIF has been saved to gallery successfully.")
    } catch (e: Exception) {
        // 处理错误情况
        Toast.makeText(context,"Failed to save GIF to gallery",Toast.LENGTH_SHORT).show()
        Log.e("GifCreation", "Failed to save GIF to gallery", e)
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Spinodal_decompositionTheme {
        val context = LocalContext.current
        StartPage(context = context) {
            
        }
    }
}
