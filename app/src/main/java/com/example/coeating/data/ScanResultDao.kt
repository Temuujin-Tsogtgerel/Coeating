package com.example.coeating.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns

@Dao
interface ScanResultDao {

    // The @RewriteQueriesToDropUnusedColumns annotation tells Room to drop columns
    // that are not mapped to your entity fields.
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM scan_results")
    suspend fun getAllScanResults(): List<ScanResultEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanResult(scanResult: ScanResultEntity): Long

    // New delete method:
    @Delete
    suspend fun deleteScanResult(scanResult: ScanResultEntity)
}
