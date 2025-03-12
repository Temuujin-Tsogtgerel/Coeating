// File: app/src/main/java/com/example/coeating/data/ScanResultEntity.kt
package com.example.coeating.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_results")
data class ScanResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "details")
    val details: String,
    // New field for the captured image's file path.
    @ColumnInfo(name = "image_path")
    val imagePath: String? = null
)
