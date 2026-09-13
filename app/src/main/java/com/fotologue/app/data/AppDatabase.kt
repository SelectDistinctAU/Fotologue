package com.fotologue.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Camera::class, Film::class, LogEntry::class, Lens::class, Teleconverter::class, Filter::class, CameraKit::class, Tag::class, LogEntryTagCrossRef::class, Flash::class, CameraLensCrossRef::class, FilmRollSession::class, Viewfinder::class], version = 33, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cameraDao(): CameraDao
    abstract fun filmDao(): FilmDao
    abstract fun logEntryDao(): LogEntryDao
    abstract fun lensDao(): LensDao
    abstract fun teleconverterDao(): TeleconverterDao
    abstract fun filterDao(): FilterDao
    abstract fun flashDao(): FlashDao
    abstract fun cameraKitDao(): CameraKitDao
    abstract fun tagDao(): TagDao
    abstract fun filmRollSessionDao(): FilmRollSessionDao
    abstract fun viewfinderDao(): ViewfinderDao

    companion object {
        val MIGRATION_29_30 = object : Migration(29, 30) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE log_entries ADD COLUMN isExportedXmp INTEGER NOT NULL DEFAULT 0")
            }
        }
        val MIGRATION_30_31 = object : Migration(30, 31) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cameras ADD COLUMN format TEXT NOT NULL DEFAULT '35mm'")
            }
        }
        val MIGRATION_31_32 = object : Migration(31, 32) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cameras ADD COLUMN triggerDeviceId TEXT")
            }
        }
        val MIGRATION_32_33 = object : Migration(32, 33) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `viewfinders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `modelNumber` TEXT NOT NULL, `position` INTEGER NOT NULL)")
                db.execSQL("ALTER TABLE log_entries ADD COLUMN viewfinderId INTEGER")
            }
        }
    }
}
