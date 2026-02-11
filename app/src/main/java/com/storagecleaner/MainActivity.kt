package com.storagecleaner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.storagecleaner.ui.AnalysisScreen
import com.storagecleaner.ui.ConfigurationScreen
import com.storagecleaner.ui.ResultsScreen
import com.storagecleaner.ui.theme.StorageCleanerTheme
import com.storagecleaner.viewmodel.StorageCleanerViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: StorageCleanerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            StorageCleanerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StorageCleanerApp(viewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            viewModel.unbindService(this@MainActivity)
        }
    }
}

@Composable
fun StorageCleanerApp(viewModel: StorageCleanerViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "configuration"
    ) {
        composable("configuration") {
            ConfigurationScreen(
                viewModel = viewModel,
                onStartAnalysis = {
                    navController.navigate("analysis") {
                        popUpTo("configuration") { inclusive = false }
                    }
                }
            )
        }

        composable("analysis") {
            AnalysisScreen(
                viewModel = viewModel,
                onScanComplete = {
                    navController.navigate("results") {
                        popUpTo("configuration") { inclusive = false }
                    }
                }
            )
        }

        composable("results") {
            ResultsScreen(viewModel = viewModel)
        }
    }
}
