package com.storagecleaner.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import com.storagecleaner.R
import com.storagecleaner.data.FileItem
import com.storagecleaner.data.ScanConfig
import com.storagecleaner.data.ScanState
import com.storagecleaner.data.SortOption
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class FileScannerService : Service() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()
    
    private var notificationManager: NotificationManager? = null
    private var scanJob: Job? = null
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "file_scanner_channel"
        private const val UPDATE_INTERVAL = 100
    }

    inner class LocalBinder : Binder() {
        fun getService(): FileScannerService = this@FileScannerService
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.notification_channel_description)
        }
        notificationManager?.createNotificationChannel(channel)
    }

    private fun createNotification(filesFound: Int): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.scanning_notification_title))
            .setContentText(getString(R.string.scanning_notification_text, filesFound))
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    fun startScanning(config: ScanConfig) {
        scanJob?.cancel()
        
        startForeground(NOTIFICATION_ID, createNotification(0))
        
        scanJob = serviceScope.launch {
            try {
                _scanState.value = ScanState.Scanning(0, 0f)
                
                val files = scanDirectory(config)
                val sortedFiles = sortFiles(files, config.sortOption)
                
                _scanState.value = ScanState.Completed(sortedFiles)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            } catch (e: Exception) {
                _scanState.value = ScanState.Error(e.message ?: "Unknown error")
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private suspend fun scanDirectory(config: ScanConfig): List<FileItem> = withContext(Dispatchers.IO) {
        val resultFiles = mutableListOf<FileItem>()
        var filesScanned = 0
        
        val rootDir = File(config.folderPath)
        if (!rootDir.exists() || !rootDir.isDirectory) {
            throw IllegalArgumentException("Invalid directory path")
        }

        val fileSequence = rootDir.walkTopDown()
            .filter { it.isFile }
        
        for (file in fileSequence) {
            if (!isActive) break
            
            try {
                val lastModified = file.lastModified()
                
                val matchesDateRange = when {
                    config.startDate != null && config.endDate != null -> 
                        lastModified >= config.startDate && lastModified <= config.endDate
                    config.startDate != null -> lastModified >= config.startDate
                    config.endDate != null -> lastModified <= config.endDate
                    else -> true
                }
                
                if (matchesDateRange) {
                    val mimeType = getMimeType(file.extension)
                    resultFiles.add(
                        FileItem(
                            path = file.absolutePath,
                            name = file.name,
                            size = file.length(),
                            creationDate = lastModified,
                            mimeType = mimeType
                        )
                    )
                }
                
                filesScanned++
                if (filesScanned % UPDATE_INTERVAL == 0) {
                    _scanState.value = ScanState.Scanning(resultFiles.size, 0f)
                    notificationManager?.notify(
                        NOTIFICATION_ID,
                        createNotification(resultFiles.size)
                    )
                }
            } catch (e: Exception) {
                continue
            }
        }
        
        resultFiles
    }

    private suspend fun sortFiles(files: List<FileItem>, sortOption: SortOption): List<FileItem> = 
        withContext(Dispatchers.Default) {
            when (sortOption) {
                SortOption.LARGEST_TO_SMALLEST -> files.sortedByDescending { it.size }
                SortOption.SMALLEST_TO_LARGEST -> files.sortedBy { it.size }
                SortOption.NEWEST_TO_OLDEST -> files.sortedByDescending { it.creationDate }
            }
        }

    private fun getMimeType(extension: String): String {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase()) 
            ?: "application/octet-stream"
    }

    fun stopScanning() {
        scanJob?.cancel()
        _scanState.value = ScanState.Idle
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        scanJob?.cancel()
        serviceScope.cancel()
    }
}
