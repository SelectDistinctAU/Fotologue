package com.fotologue.app.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class MacroCopyPreset(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val options: MacroCopyNamingOptions
) {
    companion object {
        val DEFAULT_PRESETS = listOf(
            MacroCopyPreset(
                id = "default_fujifilm",
                name = "Fujifilm RAW (_DSF)",
                options = MacroCopyNamingOptions(enabled = true, prefix = "_DSF", startNumber = 1001, digitCount = 4)
            ),
            MacroCopyPreset(
                id = "default_canon",
                name = "Canon RAW (IMG_)",
                options = MacroCopyNamingOptions(enabled = true, prefix = "IMG_", startNumber = 1001, digitCount = 4)
            ),
            MacroCopyPreset(
                id = "default_nikon",
                name = "Nikon RAW (DSC_)",
                options = MacroCopyNamingOptions(enabled = true, prefix = "DSC_", startNumber = 1001, digitCount = 4)
            ),
            MacroCopyPreset(
                id = "default_sony",
                name = "Sony RAW (_DSC)",
                options = MacroCopyNamingOptions(enabled = true, prefix = "_DSC", startNumber = 1001, digitCount = 5)
            )
        )
    }
}
