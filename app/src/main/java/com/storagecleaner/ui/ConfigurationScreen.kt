package com.storagecleaner.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.storagecleaner.data.SortOption
import com.storagecleaner.utils.PermissionUtils
import com.storagecleaner.viewmodel.StorageCleanerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    viewModel: StorageCleanerViewModel,
    onStartAnalysis: () -> Unit
) {
    val context = LocalContext.current
    val selectedPath by viewModel.selectedPath.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    val hasPermission = remember { mutableStateOf(PermissionUtils.hasStoragePermission()) }
    
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            val path = uri.path?.replace("/tree/primary:", "/storage/emulated/0/") ?: ""
            viewModel.setSelectedPath(path)
        }
    }

    LaunchedEffect(Unit) {
        hasPermission.value = PermissionUtils.hasStoragePermission()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Storage Cleaner",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (!hasPermission.value) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Storage Permission Required",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "This app needs access to manage all files on your device to scan and clean storage.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Button(
                        onClick = {
                            PermissionUtils.requestStoragePermission(context)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Grant Permission")
                    }
                }
            }
        }

        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select Folder",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Button(
                    onClick = {
                        if (hasPermission.value) {
                            folderPickerLauncher.launch(null)
                        } else {
                            showPermissionDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Folder to Clean")
                }
                
                selectedPath?.let { path ->
                    Text(
                        text = "Selected: $path",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Date Range Filter",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showStartDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = startDate?.let { formatDate(it) } ?: "Start Date"
                        )
                    }
                    
                    OutlinedButton(
                        onClick = { showEndDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = endDate?.let { formatDate(it) } ?: "End Date"
                        )
                    }
                }
                
                if (startDate != null || endDate != null) {
                    TextButton(
                        onClick = {
                            viewModel.setStartDate(null)
                            viewModel.setEndDate(null)
                        }
                    ) {
                        Text("Clear Date Filter")
                    }
                }
            }
        }

        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Sort By",
                    style = MaterialTheme.typography.titleMedium
                )
                
                SortOption.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = sortOption == option,
                                onClick = { viewModel.setSortOption(option) }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sortOption == option,
                            onClick = { viewModel.setSortOption(option) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (option) {
                                SortOption.LARGEST_TO_SMALLEST -> "Largest to Smallest"
                                SortOption.SMALLEST_TO_LARGEST -> "Smallest to Largest"
                                SortOption.NEWEST_TO_OLDEST -> "Newest to Oldest"
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                if (hasPermission.value && selectedPath != null) {
                    viewModel.startScanning(context)
                    onStartAnalysis()
                } else if (!hasPermission.value) {
                    showPermissionDialog = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = selectedPath != null && hasPermission.value
        ) {
            Text("Start Analysis", style = MaterialTheme.typography.titleMedium)
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permission Required") },
            text = { Text("Please grant storage permission to continue.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        PermissionUtils.requestStoragePermission(context)
                        showPermissionDialog = false
                    }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showStartDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                viewModel.setStartDate(date)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                viewModel.setEndDate(date)
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
