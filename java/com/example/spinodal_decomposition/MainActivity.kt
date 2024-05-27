package com.example.spinodal_decomposition

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.GifEncoder
import com.example.spinodal_decomposition.ui.theme.Spinodal_decompositionTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.exp
import kotlin.random.Random

var gifFilePath: String? = null


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Spinodal_decompositionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(this)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        deleteAllFilesInDirectory(this)
    }
}


val globalSimulationParameters = SimulationParameters()

// Define the MyApp composable, including the `NavController` and `NavHost`.
@Composable
fun App(context: Context) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "HomePage") {
        composable("StartPage") { StartPage(context,onNavigateToFinalPage = { navController.navigate("FinalPage") }) }
        composable("FinalPage") { FinalPage(context,onNavigateToHomePage = { navController.navigate("HomePage") }) }
        composable("HomePage") { HomePage(context,onNavigateToParaPage = { navController.navigate("ParaPage") }) }
        composable("ParaPage") { ParaPage(context,simulationParameters = globalSimulationParameters,onNavigateToStartPage = { navController.navigate("StartPage") }) }
    }
}



class SimulationParameters {
    var nx = 256 // number of computational grids along x direction
    var ny = nx // number of computational grids along y direction
    var dx = 2.0e-9 // spacing of computational grids [m]
    var dy = 2.0e-9 // same as dx in this case
    var c0 = 0.5 // average composition of B atom [atomic fraction]
    var R = 8.314 // gas constant
    var temp = 673 // temperature [K]
    var nsteps = 1800 // total number of time-steps

    var La = 20000.0 - 9.0 * temp
    var ac = 3.0e-14 // gradient coefficient [Jm2/mol]
    var Da = 1.0e-04 * exp(-300000.0 / R / temp) // diffusion coefficient of A atom [m2/s]
    var Db = 2.0e-05 * exp(-300000.0 / R / temp) // diffusion coefficient of B atom [m2/s]
    var dt = (dx * dx / Da) * 0.1 // time increment [s]

    // Initialize the arrays for the order parameter c at time t and t+dt
    var c = Array(nx) { DoubleArray(ny) { 0.0 } }
    var cnew = Array(nx) { DoubleArray(ny) { 0.0 } }

    fun updateSimulationParameters(newNx: Int, newNy: Int, newDx: Double, newDy: Double, newC0: Double, newTemp: Int, newNsteps: Int) {
        nx = newNx
        ny = newNy
        dx = newDx
        dy = newDy
        c0 = newC0
        temp = newTemp
        nsteps = newNsteps
        updateDependentParameters() // 更新依赖于上述参数的其他参数
    }

    private fun updateDependentParameters() {
        La = 20000.0 - 9.0 * temp
        Da = 1.0e-04 * kotlin.math.exp(-300000.0 / R / temp)
        Db = 2.0e-05 * kotlin.math.exp(-300000.0 / R / temp)
        dt = (dx * dx / Da) * 0.1
        // 根据新的参数重新初始化数组
        c = Array(nx) { DoubleArray(ny) { 0.0 } }
        cnew = Array(nx) { DoubleArray(ny) { 0.0 } }
    }
}

