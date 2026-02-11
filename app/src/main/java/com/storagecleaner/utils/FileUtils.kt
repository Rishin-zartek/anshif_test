package com.storagecleaner.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

object FileUtils {
    
    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        
        return String.format(
            Locale.US,
            "%.2f %s",
            bytes / 1024.0.pow(digitGroups.toDouble()),
            units[digitGroups]
        )
    }
    
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    fun getFileTypeIcon(mimeType: String): String {
        return when {
            mimeType.startsWith("image/") -> "🖼️"
            mimeType.startsWith("video/") -> "🎥"
            mimeType.startsWith("audio/") -> "🎵"
            mimeType.startsWith("text/") -> "📄"
            mimeType.startsWith("application/pdf") -> "📕"
            mimeType.startsWith("application/zip") || 
            mimeType.startsWith("application/x-rar") -> "📦"
            else -> "📁"
        }
    }
}
