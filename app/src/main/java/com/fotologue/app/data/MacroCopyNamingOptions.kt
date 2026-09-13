package com.fotologue.app.data

import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class MacroCopyNamingOptions(
    val enabled: Boolean = false,
    val prefix: String = "_DSF",
    val startNumber: Int = 1001,
    val digitCount: Int = 4,
    val isReverse: Boolean = false
) {
    fun formatFileName(index: Int, totalCount: Int): String {
        if (!enabled) return ""
        val num = if (isReverse) {
            startNumber + (totalCount - 1) - index
        } else {
            startNumber + index
        }
        val clampedDigitCount = digitCount.coerceIn(1, 8)
        return String.format(Locale.US, "%s%0${clampedDigitCount}d", prefix, num)
    }
}
