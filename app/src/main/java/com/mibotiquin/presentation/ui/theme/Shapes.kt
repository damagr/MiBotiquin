package com.mibotiquin.presentation.ui.theme

import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

val CalmShapes = Shapes(
    // Extra small - chips, small buttons
    extraSmall = RoundedCornerShape(8.dp),
    // Small - FAB, search bar, cards
    small = RoundedCornerShape(12.dp),
    // Medium - bottom sheets, dialogs
    medium = RoundedCornerShape(16.dp),
    // Large - full screen overlays
    large = RoundedCornerShape(28.dp),
    // Extra large - scanner overlay
    extraLarge = RoundedCornerShape(100.dp)
)