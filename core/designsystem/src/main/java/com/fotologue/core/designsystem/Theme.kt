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
    FLAT_DARCULA,
    FLAT_MAC_DARK,
    FLAT_NORD,
    SILVER_GELATIN,
    WARM_SEPIA,
    SAFELIGHT_RED
}

enum class PhotoCanvasMode(val colorHex: Long, val displayName: String) {
    ISO_MID_GRAY(0xFF505050, "ISO Mid-Gray (#505050)"),
    PITCH_BLACK(0xFF000000, "Pitch Black (#000000)"),
    FLAT_WHITE(0xFFFFFFFF, "Flat White (#FFFFFF)")
}

// 1. Flat Darcula (IntelliJ / FlatLaf Dark)
private val FlatDarculaColorScheme = darkColorScheme(
    primary = Color(0xFFAFB1B3),
    onPrimary = Color(0xFF2B2B2B),
    primaryContainer = Color(0xFF3C3F41),
    onPrimaryContainer = Color(0xFFE8E8E8),
    surface = Color(0xFF2B2B2B),
    onSurface = Color(0xFFAFB1B3),
    surfaceVariant = Color(0xFF3C3F41),
    onSurfaceVariant = Color(0xFFA9A9A9),
    background = Color(0xFF2B2B2B),
    onBackground = Color(0xFFAFB1B3)
)

// 2. Flat Mac Dark (macOS FlatLaf Dark)
private val FlatMacDarkColorScheme = darkColorScheme(
    primary = Color(0xFF3574F0),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF2B2D30),
    onPrimaryContainer = Color(0xFFDFE1E5),
    surface = Color(0xFF1E1F22),
    onSurface = Color(0xFFDFE1E5),
    surfaceVariant = Color(0xFF2B2D30),
    onSurfaceVariant = Color(0xFF9DA0A8),
    background = Color(0xFF1E1F22),
    onBackground = Color(0xFFDFE1E5)
)

// 3. Flat Nord (Arctic Cyan FlatLaf)
private val FlatNordColorScheme = darkColorScheme(
    primary = Color(0xFF88C0D0),
    onPrimary = Color(0xFF2E3440),
    primaryContainer = Color(0xFF3B4252),
    onPrimaryContainer = Color(0xFFECEFF4),
    surface = Color(0xFF2E3440),
    onSurface = Color(0xFFECEFF4),
    surfaceVariant = Color(0xFF3B4252),
    onSurfaceVariant = Color(0xFFD8DEE9),
    background = Color(0xFF2E3440),
    onBackground = Color(0xFFECEFF4)
)

// 4. Silver Gelatin (High-Contrast B&W Darkroom)
private val SilverGelatinColorScheme = darkColorScheme(
    primary = Color(0xFFE0E0E0),
    onPrimary = Color(0xFF121212),
    primaryContainer = Color(0xFF242424),
    onPrimaryContainer = Color(0xFFFFFFFF),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF242424),
    onSurfaceVariant = Color(0xFFB0B0B0),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0)
)

// 5. Warm Sepia (Vintage Toned Print)
private val WarmSepiaColorScheme = darkColorScheme(
    primary = Color(0xFFD4A373),
    onPrimary = Color(0xFF1C1917),
    primaryContainer = Color(0xFF2E2822),
    onPrimaryContainer = Color(0xFFFAEDCD),
    surface = Color(0xFF1C1917),
    onSurface = Color(0xFFFAEDCD),
    surfaceVariant = Color(0xFF2E2822),
    onSurfaceVariant = Color(0xFFD4A373),
    background = Color(0xFF1C1917),
    onBackground = Color(0xFFFAEDCD)
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
        PhotographicTheme.FLAT_DARCULA -> FlatDarculaColorScheme
        PhotographicTheme.FLAT_MAC_DARK -> FlatMacDarkColorScheme
        PhotographicTheme.FLAT_NORD -> FlatNordColorScheme
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
