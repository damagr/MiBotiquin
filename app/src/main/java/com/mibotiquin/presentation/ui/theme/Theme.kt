package com.mibotiquin.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.mibotiquin.domain.model.ExpiryStatus

@Composable
fun MiBotiquinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CalmDarkColorScheme else CalmLightColorScheme
    val context = LocalContext.current

    SideEffect {
        val window = (context as? Activity)?.window ?: return@SideEffect
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = !darkTheme
        controller.isAppearanceLightNavigationBars = !darkTheme
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CalmTypography,
        shapes = CalmShapes,
        content = content
    )
}

// Alias semánticos
val androidx.compose.material3.ColorScheme.calmAccent: Color
    get() = tertiary

val androidx.compose.material3.ColorScheme.calmAccentContainer: Color
    get() = tertiaryContainer

val androidx.compose.material3.ColorScheme.onCalmAccentContainer: Color
    get() = onTertiaryContainer

// Paleta de estado de caducidad
@Composable
fun ExpiryColors(status: ExpiryStatus): ExpiryColorSet {
    val colors = MaterialTheme.colorScheme
    return when (status) {
        ExpiryStatus.OK -> ExpiryColorSet(
            border = colors.outlineVariant,
            background = Color.Transparent,
            text = colors.onSurfaceVariant,
            icon = colors.onSurfaceVariant
        )
        ExpiryStatus.SOON -> ExpiryColorSet(
            border = colors.calmAccent,
            background = colors.calmAccentContainer.copy(alpha = 0.15f),
            text = colors.onCalmAccentContainer,
            icon = colors.calmAccent
        )
        ExpiryStatus.CRITICAL -> ExpiryColorSet(
            border = colors.calmAccent,
            background = colors.calmAccentContainer.copy(alpha = 0.25f),
            text = colors.onCalmAccentContainer,
            icon = colors.calmAccent
        )
        ExpiryStatus.EXPIRED -> ExpiryColorSet(
            border = colors.error,
            background = colors.errorContainer.copy(alpha = 0.2f),
            text = colors.onErrorContainer,
            icon = colors.error
        )
        ExpiryStatus.EMPTY -> ExpiryColorSet(
            border = colors.error,
            background = colors.errorContainer.copy(alpha = 0.15f),
            text = colors.onErrorContainer,
            icon = colors.error
        )
    }
}

data class ExpiryColorSet(
    val border: Color,
    val background: Color,
    val text: Color,
    val icon: Color
)

object CalmElevation {
    val card = 1.dp
    val fab = 6.dp
    val bottomSheet = 8.dp
}
