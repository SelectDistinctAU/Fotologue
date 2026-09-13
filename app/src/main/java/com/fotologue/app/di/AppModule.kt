package com.fotologue.app.di

import android.content.Context
import com.fotologue.app.connectivity.BleManager
import com.fotologue.app.connectivity.TriggerManager
import com.fotologue.app.data.*
import com.fotologue.app.location.LocationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepository(context)
    }

    @Provides
    @Singleton
    fun provideConfigurationManager(@ApplicationContext context: Context, database: AppDatabase): ConfigurationManager {
        return ConfigurationManager(context, database)
    }

    @Provides
    @Singleton
    fun provideGearConfigurationManager(settingsRepository: SettingsRepository): GearConfigurationManager {
        return GearConfigurationManager(settingsRepository)
    }

    @Provides
    @Singleton
    fun provideBleManager(@ApplicationContext context: Context, settingsRepository: SettingsRepository): BleManager {
        return BleManager(context, settingsRepository)
    }

    @Provides
    @Singleton
    fun provideLocationProvider(@ApplicationContext context: Context): LocationProvider {
        return LocationProvider(context)
    }

    @Provides
    @Singleton
    fun provideFotologueRepository(
        db: AppDatabase,
        cameraDao: CameraDao,
        filmDao: FilmDao,
        lensDao: LensDao,
        teleconverterDao: TeleconverterDao,
        filterDao: FilterDao,
        flashDao: FlashDao,
        logEntryDao: LogEntryDao,
        cameraKitDao: CameraKitDao,
        tagDao: TagDao,
        filmRollSessionDao: FilmRollSessionDao,
        viewfinderDao: ViewfinderDao,
        locationProvider: LocationProvider,
        settingsRepository: SettingsRepository
    ): FotologueRepository {
        return FotologueRepository(
            db, cameraDao, filmDao, lensDao, teleconverterDao, filterDao, flashDao,
            logEntryDao, cameraKitDao, tagDao, filmRollSessionDao, viewfinderDao, locationProvider, settingsRepository
        )
    }

    @Provides
    @Singleton
    fun provideTriggerManager(
        @ApplicationContext context: Context,
        bleManager: BleManager,
        locationProvider: LocationProvider,
        gearConfigurationManager: GearConfigurationManager,
        repository: FotologueRepository
    ): TriggerManager {
        return TriggerManager(context, bleManager, locationProvider, gearConfigurationManager, repository)
    }
}
