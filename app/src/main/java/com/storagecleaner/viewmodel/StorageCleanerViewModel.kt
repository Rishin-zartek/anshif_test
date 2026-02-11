package com.storagecleaner.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.storagecleaner.data.FileItem
import com.storagecleaner.data.ScanConfig
import com.storagecleaner.data.ScanState
import com.storagecleaner.data.SortOption
import com.storagecleaner.service.FileScannerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class StorageCleanerViewModel : ViewModel() {

    private val _selectedPath = MutableStateFlow<String?>(null)
    val selectedPath: StateFlow<String?> = _selectedPath.asStateFlow()

    private val _startDate = MutableStateFlow<Long?>(null)
    val startDate: StateFlow<Long?> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<Long?>(null)
    val endDate: StateFlow<Long?> = _endDate.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.LARGEST_TO_SMALLEST)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _fileList = MutableStateFlow<List<FileItem>>(emptyList())
    val fileList: StateFlow<List<FileItem>> = _fileList.asStateFlow()

    private val _selectedFiles = MutableStateFlow<Set<String>>(emptySet())
    val selectedFiles: StateFlow<Set<String>> = _selectedFiles.asStateFlow()

    private var scannerService: FileScannerService? = null
    private var serviceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as FileScannerService.LocalBinder
            scannerService = binder.getService()
            serviceBound = true
            
            viewModelScope.launch {
                scannerService?.scanState?.collect { state ->
                    _scanState.value = state
                    if (state is ScanState.Completed) {
                        _fileList.value = state.files
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            scannerService = null
            serviceBound = false
        }
    }

    fun setSelectedPath(path: String) {
        _selectedPath.value = path
    }

    fun setStartDate(date: Long?) {
        _startDate.value = date
    }

    fun setEndDate(date: Long?) {
        _endDate.value = date
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun bindService(context: Context) {
        val intent = Intent(context, FileScannerService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        if (serviceBound) {
            context.unbindService(serviceConnection)
            serviceBound = false
        }
    }

    fun startScanning(context: Context) {
        val path = _selectedPath.value ?: return
        
        val config = ScanConfig(
            folderPath = path,
            startDate = _startDate.value,
            endDate = _endDate.value,
            sortOption = _sortOption.value
        )

        val intent = Intent(context, FileScannerService::class.java)
        context.startForegroundService(intent)
        
        bindService(context)
        
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            scannerService?.startScanning(config)
        }
    }

    fun toggleFileSelection(filePath: String) {
        val currentSelected = _selectedFiles.value.toMutableSet()
        if (currentSelected.contains(filePath)) {
            currentSelected.remove(filePath)
        } else {
            currentSelected.add(filePath)
        }
        _selectedFiles.value = currentSelected
        
        _fileList.value = _fileList.value.map { file ->
            if (file.path == filePath) {
                file.copy(isSelected = !file.isSelected)
            } else {
                file
            }
        }
    }

    fun selectAllFiles() {
        _selectedFiles.value = _fileList.value.map { it.path }.toSet()
        _fileList.value = _fileList.value.map { it.copy(isSelected = true) }
    }

    fun deselectAllFiles() {
        _selectedFiles.value = emptySet()
        _fileList.value = _fileList.value.map { it.copy(isSelected = false) }
    }

    fun getSelectedFilesSize(): Long {
        return _fileList.value
            .filter { it.isSelected }
            .sumOf { it.size }
    }

    fun deleteSelectedFiles() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val filesToDelete = _fileList.value.filter { it.isSelected }
                filesToDelete.forEach { fileItem ->
                    try {
                        val file = File(fileItem.path)
                        file.delete()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                withContext(Dispatchers.Main) {
                    _fileList.value = _fileList.value.filter { !it.isSelected }
                    _selectedFiles.value = emptySet()
                }
            }
        }
    }

    fun deleteFile(filePath: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val file = File(filePath)
                    file.delete()
                    
                    withContext(Dispatchers.Main) {
                        _fileList.value = _fileList.value.filter { it.path != filePath }
                        val currentSelected = _selectedFiles.value.toMutableSet()
                        currentSelected.remove(filePath)
                        _selectedFiles.value = currentSelected
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scannerService?.stopScanning()
    }
}
