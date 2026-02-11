package com.storagecleaner.data

data class ScanConfig(
    val folderPath: String,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val sortOption: SortOption = SortOption.LARGEST_TO_SMALLEST
)
