package com.mibotiquin.presentation.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Calm Tech Light ColorScheme
val CalmLightColorScheme = lightColorScheme(
    primary = Color(0xFF2D6A4F),           // Forest green
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8F3DC),  // Light green container
    onPrimaryContainer = Color(0xFF1B4332),
    secondary = Color(0xFF407B5F),         // Muted green
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = Color(0xFF1B4332),
    tertiary = Color(0xFFD97706),          // Amber (accent/urgency)
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7), // Amber 50
    onTertiaryContainer = Color(0xFF78350F),
    error = Color(0xFFDC2626),
    onError = Color.White,
    errorContainer = Color(0xFFFEF2F2),
    onErrorContainer = Color(0xFF7F1D1D),
    background = Color(0xFFFAFAF9),        // Warm white
    onBackground = Color(0xFF1C1C1C),
    surface = Color.White,
    onSurface = Color(0xFF1C1C1C),
    surfaceVariant = Color(0xFFF5F5F4),    // Stone 100
    onSurfaceVariant = Color(0xFF525252),
    outline = Color(0xFFE7E5E4),           // Stone 200
    outlineVariant = Color(0xFFD6D3D1),    // Stone 300
    scrim = Color.Black,
    inverseSurface = Color(0xFF1C1C1E),
    inverseOnSurface = Color(0xFFF5F5F4),
    inversePrimary = Color(0xFF4ADE80)
)

// Calm Tech Dark ColorScheme
val CalmDarkColorScheme = darkColorScheme(
    primary = Color(0xFF4ADE80),           // Mint
    onPrimary = Color(0xFF1C1C1E),
    primaryContainer = Color(0xFF1B4332),
    onPrimaryContainer = Color(0xFFD8F3DC),
    secondary = Color(0xFF86EFAC),         // Light green
    onSecondary = Color(0xFF1C1C1E),
    secondaryContainer = Color(0xFF14532D),
    onSecondaryContainer = Color(0xFFD8F3DC),
    tertiary = Color(0xFFFBBF24),          // Warm amber
    onTertiary = Color(0xFF1C1C1E),
    tertiaryContainer = Color(0xFF78350F), // Amber 900
    onTertiaryContainer = Color(0xFFFEF3C7),
    error = Color(0xFFEF4444),
    onError = Color.Black,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEF2F2),
    background = Color(0xFF1C1C1E),        // Soft black
    onBackground = Color(0xFFF5F5F4),
    surface = Color(0xFF2C2C2E),
    onSurface = Color(0xFFF5F5F4),
    surfaceVariant = Color(0xFF3A3A3C),
    onSurfaceVariant = Color(0xFFA3A3A3),
    outline = Color(0xFF3F3F46),           // Zinc 700
    outlineVariant = Color(0xFF52525B),    // Zinc 600
    scrim = Color.Black,
    inverseSurface = Color(0xFFFAFAF9),
    inverseOnSurface = Color(0xFF1C1C1C),
    inversePrimary = Color(0xFF2D6A4F)
)