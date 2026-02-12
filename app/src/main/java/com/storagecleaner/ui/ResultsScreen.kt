package com.storagecleaner.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.storagecleaner.data.FileItem
import com.storagecleaner.utils.FileUtils
import com.storagecleaner.viewmodel.StorageCleanerViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    viewModel: StorageCleanerViewModel
) {
    val context = LocalContext.current
    val fileList by viewModel.fileList.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf<FileItem?>(null) }
    var showDeleteSingleDialog by remember { mutableStateOf<FileItem?>(null) }
    
    val selectedCount = fileList.count { it.isSelected }
    val selectedSize = viewModel.getSelectedFilesSize()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Storage Cleaner") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondaryContainer,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Selected: $selectedCount files",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Total Size: ${FileUtils.formatFileSize(selectedSize)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (selectedCount > 0) {
                                FilledTonalButton(
                                    onClick = { viewModel.deselectAllFiles() }
                                ) {
                                    Text("Deselect All")
                                }
                            } else {
                                FilledTonalButton(
                                    onClick = { viewModel.selectAllFiles() }
                                ) {
                                    Text("Select All")
                                }
                            }
                            
                            Button(
                                onClick = { showDeleteDialog = true },
                                enabled = selectedCount > 0,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete")
                            }
                        }
                    }
                }
            }

            if (fileList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "No files found",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(fileList, key = { it.path }) { file ->
                        FileItemRow(
                            file = file,
                            onCheckedChange = { viewModel.toggleFileSelection(file.path) },
                            onInfoClick = { showInfoDialog = file },
                            onOpenClick = {
                                try {
                                    val fileToOpen = File(file.path)
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        fileToOpen
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, file.mimeType)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Open with"))
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Cannot open file: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onDeleteClick = { showDeleteSingleDialog = file }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete Files?") },
            text = {
                Text(
                    "Are you sure you want to delete $selectedCount files totaling ${FileUtils.formatFileSize(selectedSize)}?\n\nThis action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSelectedFiles()
                        showDeleteDialog = false
                        Toast.makeText(
                            context,
                            "Deleted $selectedCount files",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    showInfoDialog?.let { file ->
        AlertDialog(
            onDismissRequest = { showInfoDialog = null },
            icon = {
                Text(
                    text = FileUtils.getFileTypeIcon(file.mimeType),
                    style = MaterialTheme.typography.displaySmall
                )
            },
            title = { Text("File Information") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoRow("Name", file.name)
                    InfoRow("Path", file.path)
                    InfoRow("Size", FileUtils.formatFileSize(file.size))
                    InfoRow("Date", FileUtils.formatDate(file.creationDate))
                    InfoRow("Type", file.mimeType)
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = null }) {
                    Text("Close")
                }
            }
        )
    }

    showDeleteSingleDialog?.let { file ->
        AlertDialog(
            onDismissRequest = { showDeleteSingleDialog = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete File?") },
            text = {
                Text(
                    "Are you sure you want to delete \"${file.name}\"?\n\nThis action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteFile(file.path)
                        showDeleteSingleDialog = null
                        Toast.makeText(
                            context,
                            "File deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteSingleDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
