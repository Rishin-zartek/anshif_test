package com.storagecleaner.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.storagecleaner.data.ScanState
import com.storagecleaner.viewmodel.StorageCleanerViewModel

@Composable
fun AnalysisScreen(
    viewModel: StorageCleanerViewModel,
    onScanComplete: () -> Unit
) {
    val scanState by viewModel.scanState.collectAsState()

    LaunchedEffect(scanState) {
        if (scanState is ScanState.Completed) {
            onScanComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val state = scanState) {
            is ScanState.Scanning -> {
                Text(
                    text = "Analyzing Files...",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Found ${state.filesFound} files",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(48.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = "You can minimize this app.\nWe will notify you when done.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            is ScanState.Error -> {
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            else -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Initializing...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
