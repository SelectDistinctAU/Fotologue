package com.fotologue.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Camera::class, Film::class, LogEntry::class, Lens::class, Teleconverter::class, Filter::class, CameraKit::class,
        Tag::class, LogEntryTagCrossRef::class, Flash::class, CameraLensCrossRef::class, FilmRollSession::class, Viewfinder::class,
        CameraViewfinderCrossRef::class, CameraTeleconverterCrossRef::class, CameraFilterCrossRef::class, CameraFlashCrossRef::class
    ],
    version = 36,
    exportSchema = false
)
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
        val MIGRATION_33_34 = object : Migration(33, 34) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `camera_viewfinder_cross_ref` (`cameraId` INTEGER NOT NULL, `viewfinderId` INTEGER NOT NULL, PRIMARY KEY(`cameraId`, `viewfinderId`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_camera_viewfinder_cross_ref_viewfinderId` ON `camera_viewfinder_cross_ref` (`viewfinderId`)")
            }
        }
        val MIGRATION_34_35 = object : Migration(34, 35) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE camera_kits ADD COLUMN viewfinderId INTEGER")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_camera_kits_viewfinderId` ON `camera_kits` (`viewfinderId`)")
            }
        }
        val MIGRATION_35_36 = object : Migration(35, 36) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `camera_teleconverter_cross_ref` (`cameraId` INTEGER NOT NULL, `teleconverterId` INTEGER NOT NULL, PRIMARY KEY(`cameraId`, `teleconverterId`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_camera_teleconverter_cross_ref_teleconverterId` ON `camera_teleconverter_cross_ref` (`teleconverterId`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `camera_filter_cross_ref` (`cameraId` INTEGER NOT NULL, `filterId` INTEGER NOT NULL, PRIMARY KEY(`cameraId`, `filterId`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_camera_filter_cross_ref_filterId` ON `camera_filter_cross_ref` (`filterId`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `camera_flash_cross_ref` (`cameraId` INTEGER NOT NULL, `flashId` INTEGER NOT NULL, PRIMARY KEY(`cameraId`, `flashId`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_camera_flash_cross_ref_flashId` ON `camera_flash_cross_ref` (`flashId`)")
            }
        }
    }
}
