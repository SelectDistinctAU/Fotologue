package com.fotologue.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fotologue.core.data.lab.InventoryItem
import com.fotologue.core.data.lab.InventoryItemDao
import com.fotologue.core.data.lab.InventoryMaster
import com.fotologue.core.data.lab.InventoryMasterDao
import com.fotologue.core.data.lab.LabCategory
import com.fotologue.core.data.lab.LabCategoryDao
import com.fotologue.core.data.lab.LabChemical
import com.fotologue.core.data.lab.LabChemicalDao
import com.fotologue.core.data.lab.LabEquipment
import com.fotologue.core.data.lab.LabEquipmentDao
import com.fotologue.core.data.lab.LabEquipmentSubCategory
import com.fotologue.core.data.lab.LabEquipmentSubCategoryDao
import com.fotologue.core.data.lab.LabItemName
import com.fotologue.core.data.lab.LabItemNameDao
import com.fotologue.core.data.lab.LabProcessEvent
import com.fotologue.core.data.lab.LabProcessEventDao
import com.fotologue.core.data.lab.LabStorageLocation
import com.fotologue.core.data.lab.LabStorageLocationDao
import com.fotologue.core.data.lab.LabSupplier
import com.fotologue.core.data.lab.LabSupplierDao
import com.fotologue.core.data.lab.LabUnit
import com.fotologue.core.data.lab.LabUnitDao
import com.fotologue.core.data.lab.OrderHistory
import com.fotologue.core.data.lab.OrderHistoryDao
import com.fotologue.core.data.lab.PerformanceLog
import com.fotologue.core.data.lab.PerformanceLogDao
import com.fotologue.core.data.lab.PrintOutcome
import com.fotologue.core.data.lab.PrintOutcomeDao
import com.fotologue.core.data.lab.PrintRecipe
import com.fotologue.core.data.lab.PrintRecipeDao
import com.fotologue.core.data.lab.StockLot
import com.fotologue.core.data.lab.StockLotDao
import com.fotologue.core.data.lab.SupplierIssueLog
import com.fotologue.core.data.lab.SupplierIssueLogDao

@Database(
    entities = [
        Camera::class, Film::class, LogEntry::class, Lens::class, Teleconverter::class, Filter::class, CameraKit::class,
        Tag::class, LogEntryTagCrossRef::class, Flash::class, CameraLensCrossRef::class, FilmRollSession::class, Viewfinder::class,
        CameraViewfinderCrossRef::class, CameraTeleconverterCrossRef::class, CameraFilterCrossRef::class, CameraFlashCrossRef::class,
        LabEquipment::class, LabEquipmentSubCategory::class, LabChemical::class, LabSupplier::class, SupplierIssueLog::class, InventoryItem::class, InventoryMaster::class, StockLot::class,
        LabCategory::class, LabUnit::class, LabItemName::class, OrderHistory::class, PerformanceLog::class, LabProcessEvent::class, PrintRecipe::class,
        LabStorageLocation::class, PrintOutcome::class
    ],
    version = 48,
    exportSchema = true
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

    abstract fun labEquipmentDao(): LabEquipmentDao
    abstract fun labEquipmentSubCategoryDao(): LabEquipmentSubCategoryDao
    abstract fun labChemicalDao(): LabChemicalDao
    abstract fun labSupplierDao(): LabSupplierDao
    abstract fun supplierIssueLogDao(): SupplierIssueLogDao
    abstract fun labCategoryDao(): LabCategoryDao
    abstract fun labUnitDao(): LabUnitDao
    abstract fun labItemNameDao(): LabItemNameDao
    abstract fun inventoryMasterDao(): InventoryMasterDao
    abstract fun stockLotDao(): StockLotDao
    abstract fun inventoryItemDao(): InventoryItemDao
    abstract fun orderHistoryDao(): OrderHistoryDao
    abstract fun performanceLogDao(): PerformanceLogDao
    abstract fun labProcessEventDao(): LabProcessEventDao
    abstract fun printRecipeDao(): PrintRecipeDao
    abstract fun labStorageLocationDao(): LabStorageLocationDao
    abstract fun printOutcomeDao(): PrintOutcomeDao

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
        val MIGRATION_36_37 = object : Migration(36, 37) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_equipment` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `notes` TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_chemicals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `dilution` TEXT NOT NULL, `mixDate` INTEGER NOT NULL, `capacityRollsTotal` INTEGER NOT NULL, `capacityRollsUsed` INTEGER NOT NULL, `isExhausted` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_process_events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `filmRollId` TEXT NOT NULL, `developerName` TEXT NOT NULL, `temperatureCelsius` REAL NOT NULL, `durationSeconds` INTEGER NOT NULL, `agitationSchedule` TEXT NOT NULL, `pushPullStops` REAL NOT NULL)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_lab_process_events_filmRollId` ON `lab_process_events` (`filmRollId`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `print_recipes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `logEntryId` INTEGER NOT NULL, `paperName` TEXT NOT NULL, `paperSize` TEXT NOT NULL, `enlargerLens` TEXT NOT NULL, `baseExposureSeconds` REAL NOT NULL, `gradeFilter` TEXT NOT NULL, `dodgeBurnMapUri` TEXT, FOREIGN KEY(`logEntryId`) REFERENCES `log_entries`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_print_recipes_logEntryId` ON `print_recipes` (`logEntryId`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_storage_locations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `sleeveNumber` TEXT NOT NULL, `locationType` TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `print_outcomes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `printRecipeId` INTEGER NOT NULL, `editionNumber` TEXT NOT NULL, `status` TEXT NOT NULL, `exhibitionName` TEXT, `buyerName` TEXT, `salePrice` TEXT, `saleDate` INTEGER, `notes` TEXT NOT NULL, FOREIGN KEY(`printRecipeId`) REFERENCES `print_recipes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_print_outcomes_printRecipeId` ON `print_outcomes` (`printRecipeId`)")
            }
        }
        val MIGRATION_37_38 = object : Migration(37, 38) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE lab_chemicals ADD COLUMN quantityOnHand INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE lab_chemicals ADD COLUMN expirationDate INTEGER")
                db.execSQL("ALTER TABLE lab_chemicals ADD COLUMN supplier TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE lab_chemicals ADD COLUMN cost TEXT NOT NULL DEFAULT ''")
            }
        }
        val MIGRATION_38_39 = object : Migration(38, 39) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE lab_chemicals ADD COLUMN volumeAmount REAL NOT NULL DEFAULT 1.0")
                db.execSQL("ALTER TABLE lab_chemicals ADD COLUMN volumeUnit TEXT NOT NULL DEFAULT 'L'")
            }
        }
        val MIGRATION_39_40 = object : Migration(39, 40) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_suppliers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `region` TEXT NOT NULL DEFAULT 'Australia', `website` TEXT NOT NULL DEFAULT '', `email` TEXT NOT NULL DEFAULT '', `phone` TEXT NOT NULL DEFAULT '', `address` TEXT NOT NULL DEFAULT '', `reliabilityRating` REAL NOT NULL DEFAULT 5.0, `notes` TEXT NOT NULL DEFAULT '')")
                db.execSQL("ALTER TABLE lab_chemicals ADD COLUMN unopenedQuantity INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE lab_chemicals ADD COLUMN openedQuantity INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE lab_chemicals ADD COLUMN userRating REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE lab_chemicals ADD COLUMN performanceNotes TEXT NOT NULL DEFAULT ''")
            }
        }
        val MIGRATION_40_41 = object : Migration(40, 41) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_inventory_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `type` TEXT NOT NULL DEFAULT '', `manufacturer` TEXT NOT NULL DEFAULT '', `containerSize` TEXT NOT NULL DEFAULT '1 L', `supplierId` INTEGER, `cost` TEXT NOT NULL DEFAULT '', `quantityOnHand` INTEGER NOT NULL DEFAULT 1, `quantityInUse` INTEGER NOT NULL DEFAULT 0, `quantityInTransit` INTEGER NOT NULL DEFAULT 0, `expiryDate` INTEGER, `notes` TEXT NOT NULL DEFAULT '', FOREIGN KEY(`supplierId`) REFERENCES `lab_suppliers`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_lab_inventory_items_supplierId` ON `lab_inventory_items` (`supplierId`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_order_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `supplierId` INTEGER, `orderDate` INTEGER NOT NULL, `status` TEXT NOT NULL DEFAULT 'IN_TRANSIT', `totalCost` TEXT NOT NULL DEFAULT '', `trackingNumber` TEXT NOT NULL DEFAULT '', `notes` TEXT NOT NULL DEFAULT '', FOREIGN KEY(`supplierId`) REFERENCES `lab_suppliers`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_lab_order_history_supplierId` ON `lab_order_history` (`supplierId`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_performance_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `logEntryId` INTEGER NOT NULL, `inventoryItemId` INTEGER NOT NULL, `outcomeRating` REAL NOT NULL DEFAULT 5.0, `notes` TEXT NOT NULL DEFAULT '', FOREIGN KEY(`logEntryId`) REFERENCES `log_entries`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`inventoryItemId`) REFERENCES `lab_inventory_items`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_lab_performance_logs_logEntryId` ON `lab_performance_logs` (`logEntryId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_lab_performance_logs_inventoryItemId` ON `lab_performance_logs` (`inventoryItemId`)")
            }
        }
        val MIGRATION_41_42 = object : Migration(41, 42) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE lab_inventory_items ADD COLUMN batchNumber TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE lab_performance_logs ADD COLUMN outcomeStatus TEXT NOT NULL DEFAULT 'KEEPER'")
                db.execSQL("ALTER TABLE lab_performance_logs ADD COLUMN rejectCategory TEXT")
                db.execSQL("ALTER TABLE lab_performance_logs ADD COLUMN systemErrorReason TEXT")
                db.execSQL("ALTER TABLE lab_performance_logs ADD COLUMN learningTag TEXT")
            }
        }
        val MIGRATION_42_43 = object : Migration(42, 43) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_inventory_masters` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `category` TEXT NOT NULL, `name` TEXT NOT NULL, `manufacturer` TEXT NOT NULL DEFAULT '', `containerSize` TEXT NOT NULL DEFAULT '1 L', `defaultLocation` TEXT NOT NULL DEFAULT 'Chemical Shelf', `notes` TEXT NOT NULL DEFAULT '')")
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_stock_lots` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `masterId` INTEGER NOT NULL, `supplierId` INTEGER, `batchNumber` TEXT NOT NULL DEFAULT '', `cost` TEXT NOT NULL DEFAULT '', `expiryDate` INTEGER, `openedDate` INTEGER, `status` TEXT NOT NULL DEFAULT 'ON_HAND', `quantity` INTEGER NOT NULL DEFAULT 1, `purchaseDate` INTEGER NOT NULL, `notes` TEXT NOT NULL DEFAULT '', FOREIGN KEY(`masterId`) REFERENCES `lab_inventory_masters`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`supplierId`) REFERENCES `lab_suppliers`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_lab_stock_lots_masterId` ON `lab_stock_lots` (`masterId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_lab_stock_lots_supplierId` ON `lab_stock_lots` (`supplierId`)")
            }
        }
        val MIGRATION_43_44 = object : Migration(43, 44) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE lab_stock_lots ADD COLUMN location TEXT NOT NULL DEFAULT 'Chemical Shelf'")
                db.execSQL("ALTER TABLE lab_suppliers ADD COLUMN isUsed INTEGER NOT NULL DEFAULT 0")
            }
        }
        val MIGRATION_44_45 = object : Migration(44, 45) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `defaultUnit` TEXT NOT NULL DEFAULT 'L', `isSystemCore` INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_units` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `isSystemCore` INTEGER NOT NULL DEFAULT 0)")
            }
        }
        val MIGRATION_45_46 = object : Migration(45, 46) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_item_names` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL DEFAULT 'Chemistry')")
            }
        }
        val MIGRATION_46_47 = object : Migration(46, 47) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_equipment_subcategories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `category` TEXT NOT NULL, `name` TEXT NOT NULL)")
            }
        }
        val MIGRATION_47_48 = object : Migration(47, 48) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `lab_supplier_issue_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `supplierId` INTEGER NOT NULL, `date` INTEGER NOT NULL, `issueSummary` TEXT NOT NULL, `status` TEXT NOT NULL DEFAULT 'OPEN', `contactPerson` TEXT NOT NULL DEFAULT '', `resolutionReason` TEXT NOT NULL DEFAULT '', `notes` TEXT NOT NULL DEFAULT '', FOREIGN KEY(`supplierId`) REFERENCES `lab_suppliers`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_lab_supplier_issue_logs_supplierId` ON `lab_supplier_issue_logs` (`supplierId`)")
            }
        }
    }
}
