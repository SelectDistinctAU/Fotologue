package com.fotologue.core.data.migration

import com.fotologue.core.data.AppDatabase
import com.fotologue.core.data.lab.LabPresets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class DatabaseSchemaUnitTest {

    @Test
    fun `AppDatabase migrations 36 to 42 are correctly configured`() {
        assertNotNull(AppDatabase.MIGRATION_36_37)
        assertNotNull(AppDatabase.MIGRATION_41_42)
        assertEquals(36, AppDatabase.MIGRATION_36_37.startVersion)
        assertEquals(37, AppDatabase.MIGRATION_36_37.endVersion)
        assertEquals(41, AppDatabase.MIGRATION_41_42.startVersion)
        assertEquals(42, AppDatabase.MIGRATION_41_42.endVersion)
    }

    @Test
    fun `LabPresets contains valid equipment and chemical presets`() {
        assertEquals(true, LabPresets.equipmentPresets.isNotEmpty())
        assertEquals(true, LabPresets.chemicalPresets.isNotEmpty())
        assertEquals(true, LabPresets.paperPresets.isNotEmpty())
    }
}
