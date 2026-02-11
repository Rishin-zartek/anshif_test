package com.storagecleaner.data

sealed class ScanState {
    object Idle : ScanState()
    data class Scanning(val filesFound: Int, val progress: Float) : ScanState()
    data class Completed(val files: List<FileItem>) : ScanState()
    data class Error(val message: String) : ScanState()
}
