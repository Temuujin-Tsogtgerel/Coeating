// file: app/src/main/java/com/example/coeating/data/ScanRepository.kt
package com.example.coeating.data

import android.content.Context
import androidx.room.Room

class ScanRepository(context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "coeating_db"
    ).build()

    private val scanResultDao = db.scanResultDao()

    suspend fun getAllScans(): List<ScanResultEntity> = scanResultDao.getAllScanResults()

    suspend fun insertScan(scan: ScanResultEntity) = scanResultDao.insertScanResult(scan)

    suspend fun deleteScan(scan: ScanResultEntity) = scanResultDao.deleteScanResult(scan)
}
