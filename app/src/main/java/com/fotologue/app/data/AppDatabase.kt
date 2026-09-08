package com.fotologue.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Camera::class, Film::class, LogEntry::class, Lens::class, Teleconverter::class, Filter::class, CameraKit::class, Tag::class, LogEntryTagCrossRef::class, Flash::class, CameraLensCrossRef::class], version = 26, exportSchema = false)
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
}
