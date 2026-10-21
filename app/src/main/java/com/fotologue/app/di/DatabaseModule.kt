package com.fotologue.app.di

import android.content.Context
import androidx.room.Room
import com.fotologue.core.data.*
import com.fotologue.core.data.lab.*
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
            .addMigrations(
                AppDatabase.MIGRATION_29_30,
                AppDatabase.MIGRATION_30_31,
                AppDatabase.MIGRATION_31_32,
                AppDatabase.MIGRATION_32_33,
                AppDatabase.MIGRATION_33_34,
                AppDatabase.MIGRATION_34_35,
                AppDatabase.MIGRATION_35_36,
                AppDatabase.MIGRATION_36_37,
                AppDatabase.MIGRATION_37_38,
                AppDatabase.MIGRATION_38_39,
                AppDatabase.MIGRATION_39_40,
                AppDatabase.MIGRATION_40_41,
                AppDatabase.MIGRATION_41_42,
                AppDatabase.MIGRATION_42_43,
                AppDatabase.MIGRATION_43_44,
                AppDatabase.MIGRATION_44_45,
                AppDatabase.MIGRATION_45_46,
                AppDatabase.MIGRATION_46_47,
                AppDatabase.MIGRATION_47_48,
                AppDatabase.MIGRATION_48_49,
                AppDatabase.MIGRATION_49_50,
                AppDatabase.MIGRATION_50_51
            )
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

    @Provides
    fun provideFilmRollSessionDao(db: AppDatabase): FilmRollSessionDao = db.filmRollSessionDao()

    @Provides
    fun provideViewfinderDao(db: AppDatabase): ViewfinderDao = db.viewfinderDao()

    @Provides
    fun provideLabEquipmentDao(db: AppDatabase): LabEquipmentDao = db.labEquipmentDao()

    @Provides
    fun provideLabChemicalDao(db: AppDatabase): LabChemicalDao = db.labChemicalDao()

    @Provides
    fun provideLabSupplierDao(db: AppDatabase): LabSupplierDao = db.labSupplierDao()

    @Provides
    fun provideLabCategoryDao(db: AppDatabase): LabCategoryDao = db.labCategoryDao()

    @Provides
    fun provideLabUnitDao(db: AppDatabase): LabUnitDao = db.labUnitDao()

    @Provides
    fun provideLabItemNameDao(db: AppDatabase): LabItemNameDao = db.labItemNameDao()

    @Provides
    fun provideLabEquipmentSubCategoryDao(db: AppDatabase): LabEquipmentSubCategoryDao = db.labEquipmentSubCategoryDao()

    @Provides
    fun provideSupplierIssueLogDao(db: AppDatabase): SupplierIssueLogDao = db.supplierIssueLogDao()

    @Provides
    fun provideInventoryMasterDao(db: AppDatabase): InventoryMasterDao = db.inventoryMasterDao()

    @Provides
    fun provideStockLotDao(db: AppDatabase): StockLotDao = db.stockLotDao()

    @Provides
    fun provideInventoryItemDao(db: AppDatabase): InventoryItemDao = db.inventoryItemDao()

    @Provides
    fun provideOrderHistoryDao(db: AppDatabase): OrderHistoryDao = db.orderHistoryDao()

    @Provides
    fun providePerformanceLogDao(db: AppDatabase): PerformanceLogDao = db.performanceLogDao()

    @Provides
    fun provideLabProcessEventDao(db: AppDatabase): LabProcessEventDao = db.labProcessEventDao()

    @Provides
    fun providePrintRecipeDao(db: AppDatabase): PrintRecipeDao = db.printRecipeDao()

    @Provides
    fun provideLabStorageLocationDao(db: AppDatabase): LabStorageLocationDao = db.labStorageLocationDao()

    @Provides
    fun providePrintOutcomeDao(db: AppDatabase): PrintOutcomeDao = db.printOutcomeDao()
}
