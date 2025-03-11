// file: app/src/main/java/com/example/coeating/data/AppDatabase.kt
package com.example.coeating.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ScanResultEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanResultDao(): ScanResultDao
}
