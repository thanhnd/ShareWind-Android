package com.sharewind.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape tokens from [docs/ui-mockups.md](https://github.com/lyhuynh323/ShareWind/blob/main/docs/ui-mockups.md).
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp)
)

/** Cards & modals — 16dp */
val CardShape = RoundedCornerShape(16.dp)

/** Buttons — 12dp */
val ButtonShape = RoundedCornerShape(12.dp)

/** Pill shape for filter chips, search bar — 99dp */
val PillShape = RoundedCornerShape(99.dp)
