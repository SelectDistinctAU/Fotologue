package com.fotologue.core.designsystem

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class PhotographicTheme {
    SYSTEM_DEFAULT,
    DAYLIGHT_LIGHT,
    SILVER_GELATIN,
    WARM_SEPIA,
    SAFELIGHT_RED
}

enum class PhotoCanvasMode(val colorHex: Long, val displayName: String) {
    ISO_MID_GRAY(0xFF505050, "ISO Mid-Gray (#505050)"),
    PITCH_BLACK(0xFF000000, "Pitch Black (#000000)"),
    FLAT_WHITE(0xFFFFFFFF, "Flat White (#FFFFFF)")
}

// 0. Daylight Clean (Light Theme Interface)
private val DaylightLightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = Color(0xFF0D47A1),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = Color(0xFF44474E),
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1C1B1F)
)

// 1. Silver Gelatin (High-Contrast B&W Darkroom)
private val SilverGelatinColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF333333),
    onPrimaryContainer = Color(0xFFFFFFFF),
    surface = Color(0xFF141414),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF262626),
    onSurfaceVariant = Color(0xFFE5E5E5),
    background = Color(0xFF141414),
    onBackground = Color(0xFFFFFFFF)
)

// 5. Warm Sepia (Vintage Toned Print)
private val WarmSepiaColorScheme = darkColorScheme(
    primary = Color(0xFFD4A373),
    onPrimary = Color(0xFF191512),
    primaryContainer = Color(0xFF3D3024),
    onPrimaryContainer = Color(0xFFF5E6D3),
    surface = Color(0xFF191512),
    onSurface = Color(0xFFF5E6D3),
    surfaceVariant = Color(0xFF2A221C),
    onSurfaceVariant = Color(0xFFE6CBB2),
    background = Color(0xFF191512),
    onBackground = Color(0xFFF5E6D3)
)

// 6. Safelight Red (In-situ Darkroom)
private val SafelightRedColorScheme = darkColorScheme(
    primary = Color(0xFFFF0000),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF400000),
    onPrimaryContainer = Color(0xFFFF6666),
    surface = Color(0xFF000000),
    onSurface = Color(0xFFFF0000),
    surfaceVariant = Color(0xFF1F0000),
    onSurfaceVariant = Color(0xFFFF3333),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFF0000)
)

private val DefaultDarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val DefaultLightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun FotologueTheme(
    photographicTheme: PhotographicTheme = PhotographicTheme.SYSTEM_DEFAULT,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when (photographicTheme) {
        PhotographicTheme.DAYLIGHT_LIGHT -> DaylightLightColorScheme
        PhotographicTheme.SILVER_GELATIN -> SilverGelatinColorScheme
        PhotographicTheme.WARM_SEPIA -> WarmSepiaColorScheme
        PhotographicTheme.SAFELIGHT_RED -> SafelightRedColorScheme
        PhotographicTheme.SYSTEM_DEFAULT -> {
            when {
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                }
                darkTheme -> DefaultDarkColorScheme
                else -> DefaultLightColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
