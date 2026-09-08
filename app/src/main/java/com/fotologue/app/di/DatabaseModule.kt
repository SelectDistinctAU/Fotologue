package com.fotologue.app.di

import android.content.Context
import androidx.room.Room
import com.fotologue.app.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "fotologue_database")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideCameraDao(db: AppDatabase): CameraDao = db.cameraDao()

    @Provides
    fun provideFilmDao(db: AppDatabase): FilmDao = db.filmDao()

    @Provides
    fun provideLensDao(db: AppDatabase): LensDao = db.lensDao()

    @Provides
    fun provideTeleconverterDao(db: AppDatabase): TeleconverterDao = db.teleconverterDao()

    @Provides
    fun provideFilterDao(db: AppDatabase): FilterDao = db.filterDao()

    @Provides
    fun provideFlashDao(db: AppDatabase): FlashDao = db.flashDao()

    @Provides
    fun provideLogEntryDao(db: AppDatabase): LogEntryDao = db.logEntryDao()

    @Provides
    fun provideCameraKitDao(db: AppDatabase): CameraKitDao = db.cameraKitDao()

    @Provides
    fun provideTagDao(db: AppDatabase): TagDao = db.tagDao()
}
