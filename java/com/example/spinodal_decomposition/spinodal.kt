package com.example.spinodal_decomposition

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.GifEncoder
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random




fun updateOrderParameter(c: Array<DoubleArray>, cnew: Array<DoubleArray>,ny: Int,nx:Int,R:Double,temp:Int,La:Double,ac:Double,dx:Double,dy:Double,Db:Double,dt:Double,Da:Double ) {
    for (j in 0 until ny) {
        for (i in 0 until nx) {

            var ip = i + 1
            var im = i - 1
            var jp = j + 1
            var jm = j - 1
            var ipp = i + 2
            var imm = i - 2
            var jpp = j + 2
            var jmm = j - 2

            // Apply periodic boundary conditions
            if (ip > nx - 1) ip -= nx
            if (im < 0) im += nx
            if (jp > ny - 1) jp -= ny
            if (jm < 0) jm += ny
            if (ipp > nx - 1) ipp -= nx
            if (imm < 0) imm += nx
            if (jpp > ny - 1) jpp -= ny
            if (jmm < 0) jmm += ny

            // Retrieving the values at various points
            val cc = c[i][j] // central point
            val ce = c[ip][j] // eastern point
            val cw = c[im][j] // western point
            val cs = c[i][jm] // southern point
            val cn = c[i][jp] // northern point
            val cse = c[ip][jm] // southeastern point
            val cne = c[ip][jp] // northeastern point
            val csw = c[im][jm] // southwestern point
            val cnw = c[im][jp] // northwestern point
            val cee = c[ipp][j] // east-eastern point
            val cww = c[imm][j] // west-western point
            val css = c[i][jmm] // south-southern point
            val cnn = c[i][jpp] // north-northern point

            // Chemical term of the diffusion potential
            val muChemC = R * temp * (Math.log(cc) - Math.log(1.0 - cc)) + La * (1.0 - 2.0 * cc)
            val muChemW = R * temp * (Math.log(cw) - Math.log(1.0 - cw)) + La * (1.0 - 2.0 * cw)
            val muChemE = R * temp * (Math.log(ce) - Math.log(1.0 - ce)) + La * (1.0 - 2.0 * ce)
            val muChemN = R * temp * (Math.log(cn) - Math.log(1.0 - cn)) + La * (1.0 - 2.0 * cn)
            val muChemS = R * temp * (Math.log(cs) - Math.log(1.0 - cs)) + La * (1.0 - 2.0 * cs)

            // Gradient term of the diffusion potential
            val muGradC = -ac * ((ce - 2.0 * cc + cw) / (dx * dx) + (cn - 2.0 * cc + cs) / (dy * dy))
            val muGradW = -ac * ((cc - 2.0 * cw + cww) / (dx * dx) + (cnw - 2.0 * cw + csw) / (dy * dy))
            val muGradE = -ac * ((cee - 2.0 * ce + cc) / (dx * dx) + (cne - 2.0 * ce + cse) / (dy * dy))
            val muGradN = -ac * ((cne - 2.0 * cn + cnw) / (dx * dx) + (cnn - 2.0 * cn + cc) / (dy * dy))
            val muGradS = -ac * ((cse - 2.0 * cs + csw) / (dx * dx) + (cc - 2.0 * cs + css) / (dy * dy))

            // Total diffusion potential
            val muC = muChemC + muGradC
            val muW = muChemW + muGradW
            val muE = muChemE + muGradE
            val muN = muChemN + muGradN
            val muS = muChemS + muGradS

            // Calculations for the Laplacian of the chemical potential and second-order partial derivatives
            val nablaMu = (muW - 2.0 * muC + muE) / (dx * dx) + (muN - 2.0 * muC + muS) / (dy * dy)
            val dc2dx2 = ((ce - cw) * (muE - muW)) / (4.0 * dx * dx)
            val dc2dy2 = ((cn - cs) * (muN - muS)) / (4.0 * dy * dy)

            // Mobility and its derivative with respect to concentration
            val DbDa = Db / Da
            val mob = (Da / R / temp) * (cc + DbDa * (1.0 - cc)) * cc * (1.0 - cc)
            val dmdc = (Da / R / temp) * ((1.0 - DbDa) * cc * (1.0 - cc) + (cc + DbDa * (1.0 - cc)) * (1.0 - 2.0 * cc))

            // Right-hand side of the Cahn-Hilliard equation
            val dcdt = mob * nablaMu + dmdc * (dc2dx2 + dc2dy2)

            // Update the order parameter c
            cnew[i][j] = c[i][j] + dcdt * dt
        }
    }
}

fun initial_frame(): Bitmap {
    val params = globalSimulationParameters
    val prin = params.nx
    println("Get Para nx:$prin")
    // 创建和填充矩阵
    val c = Array(params.nx) { DoubleArray(params.ny) { params.c0 + Random.nextDouble() * 0.01 } }

    val cList = c.flatMap { it.toList() }
    val cMax = cList.maxOrNull() ?: 0.0
    val cMin = cList.minOrNull() ?: 1.0
    println("Maximum concentration = $cMax")
    println("Minimum concentration = $cMin")

    // 创建Bitmap对象替代BufferedImage
    val bitmap = Bitmap.createBitmap(params.nx, params.ny, Bitmap.Config.ARGB_8888)

    // 使用Bitmap设置像素颜色
    for (x in 0 until params.nx) {
        for (y in 0 until params.ny) {
            // 计算归一化值
            val normalizedValue = (c[x][y] - cMin) / (cMax - cMin)
            // 映射归一化值到饱和度，保持色调（蓝色）和亮度（最大值）
            val hsv = floatArrayOf(240f, normalizedValue.toFloat(), 0.9f) // 色调需要乘以360，因为Android期望的是度数
            val color = Color.HSVToColor(hsv)
            bitmap.setPixel(x, y, color)
        }
    }

    return bitmap
}

fun circulation(context: Context, progressViewModel: ProgressViewModel)
{
    val params = globalSimulationParameters
    var progressvalue = 0f
    // 创建和填充矩阵
    val c = Array(params.nx) { DoubleArray(params.ny) { params.c0 + Random.nextDouble() * 0.01 } }
    // 循环nsteps步
    for (nstep in 1..params.nsteps) {
        // 更新参数
        updateOrderParameter(c, params.cnew,params.ny,params.nx,params.R,params.temp,params.La,params.ac,params.dx,params.dy,params.Db,params.dt,params.Da)
        // 将cNew的值赋给c
        for (x in 0 until params.nx) {
            for (y in 0 until params.ny) {
                c[x][y] = params.cnew[x][y]
            }
        }
        progressvalue = nstep/params.nsteps.toFloat()
        progressViewModel.updateProgress(progressvalue)

        // 每600步打印状态并保存图像
        if (nstep % 600 == 0) {
            println("nstep = $nstep")
            val cList = c.flatMap { it.toList() }
            val maxC = cList.maxOrNull() ?: 0.0
            val minC = cList.minOrNull() ?: 1.0
            println("Maximum concentration = $maxC")
            println("Minimum concentration = $minC")

            // 创建Bitmap对象替代BufferedImage
            val bitmap = Bitmap.createBitmap(params.nx, params.ny, Bitmap.Config.ARGB_8888)

            val cMin = minC
            val cMax = maxC

            // 使用Bitmap设置像素颜色
            for (x in 0 until params.nx) {
                for (y in 0 until params.ny) {
                    // 计算归一化值
                    val normalizedValue = (c[x][y] - cMin) / (cMax - cMin)
                    // 映射归一化值到饱和度，保持色调（蓝色）和亮度（最大值）
                    val hsv = floatArrayOf(0.667f * 360, normalizedValue.toFloat(), 1.0f) // 色调需要乘以360，因为Android期望的是度数
                    val color = Color.HSVToColor(hsv)
                    bitmap.setPixel(x, y, color)
                }
            }
            saveBitmapToSpecificDirectory(context, bitmap, "concentration_${nstep}")

        }
    }
}

fun saveBitmapToSpecificDirectory(context: Context, bitmap: Bitmap, displayName: String) {
    val imageFileName = "$displayName.jpg"

    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/SC/temp/")
    if (!storageDir?.exists()!!) {
        storageDir.mkdirs()
    }

    val imageFile = File(storageDir, imageFileName)
    try {
        FileOutputStream(imageFile).use { out ->
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                Log.d("SaveImage", "图片已成功保存到：${imageFile.absolutePath}")
            } else {
                Log.d("SaveImage", "无法压缩bitmap，保存失败")
            }
        }
    } catch (e: IOException) {
        Log.d("SaveImage", "保存图片时出现异常：", e)
    }
}

fun deleteAllFilesInDirectory(context: Context) {
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/SC/temp/")

    if (storageDir?.exists() == true) {
        val files = storageDir.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isFile) { // 确保不是目录
                    val deleted = file.delete()
                    if (deleted) {
                        Log.d("DeleteFiles", "文件已被删除：${file.absolutePath}")
                    } else {
                        Log.d("DeleteFiles", "无法删除文件：${file.absolutePath}")
                    }
                }
            }
            Log.d("DeleteFiles", "所有文件已尝试删除。")
        } else {
            Log.d("DeleteFiles", "目录为空或无法读取。")
        }
    } else {
        Log.d("DeleteFiles", "指定的目录不存在：${storageDir?.absolutePath}")
    }
}


fun encodeGifFromSavedImages(context: Context) {
    val gifFileName = "AnimatedGif.gif" // 定义GIF文件的名称
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/GIFs") // 指定存储目录

    // 确保存储目录存在
    if (!storageDir?.exists()!!) {
        storageDir.mkdirs()
    }

    val outFile = File(storageDir, gifFileName) // 创建File对象表示输出文件的完整路径


    try {
        val gifEncoder = GifEncoder()
        gifEncoder.start(outFile.getCanonicalPath())
        gifEncoder.setDelay(500) // 500ms between frames

        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/SC/temp/")
        val imageFiles = storageDir?.listFiles { _, name -> name.startsWith("concentration_") && name.endsWith(".jpg") }

        // 对文件进行排序
        val sortedFiles = imageFiles?.sortedBy { file ->
            file.name.substringAfter("concentration_").substringBefore(".jpg").toIntOrNull() ?: Int.MAX_VALUE
        }

        sortedFiles?.forEach { imageFile ->
            val bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
            gifEncoder.addFrame(bitmap)
        }

        gifEncoder.finish()

        Log.d("GifCreation", "GIF has been saved to: ${outFile.absolutePath}")
        gifFilePath = outFile.absolutePath


    } catch (err: IOException) {
        Log.e("GifCreation", "Error creating GIF", err)
    }
}



