package com.storagecleaner.data

data class FileItem(
    val path: String,
    val name: String,
    val size: Long,
    val creationDate: Long,
    val mimeType: String,
    val isSelected: Boolean = false
)
