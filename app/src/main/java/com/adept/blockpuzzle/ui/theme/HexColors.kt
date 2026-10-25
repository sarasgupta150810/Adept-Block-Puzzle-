package com.adept.blockpuzzle.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object HexColors {
    // App Background: Radiant Sky Blue Bokeh
    val AppBackgroundTop = Color(0xFF90D5FF)
    val AppBackgroundMid = Color(0xFFC3EAFF)
    val AppBackgroundBottom = Color(0xFFE2F4FF)

    // Game Board: Dark Slate Hexagon Base
    val BoardBackdrop = Color(0xFF222C42)
    val BoardBorder = Color(0xFF324160)
    val EmptyCellFill = Color(0xFF1B2338)
    val EmptyCellBorder = Color(0xFF2B3856)
    val LockedCellFill = Color(0xFF334155)
    val LockedCellBorder = Color(0xFF475569)

    // Next Blocks Tray
    val TrayBackground = Color(0xFF25354E)
    val TrayBorder = Color(0xFF384E73)
    val TraySlotBackground = Color(0xFF1A263B)

    // Header score bar
    val HeaderCardBackground = Color(0xFF1E2B46)
    val HeaderCardBorder = Color(0xFF3B82F6)

    val AccentGreen = Color(0xFF22C55E)
    val AccentGreenLight = Color(0xFF86EFAC)
    val AccentGreenBright = Color(0xFF69F0AE)
    val AccentGreenDark = Color(0xFF15803D)

    val AccentBlue = Color(0xFF3B82F6)
    val AccentBlueLight = Color(0xFF93C5FD)
    val AccentBlueDark = Color(0xFF1D4ED8)

    val AccentRed = Color(0xFFEF4444)
    val AccentRedLight = Color(0xFFFCA5A5)
    val AccentRedDark = Color(0xFFB91C1C)

    val AccentYellow = Color(0xFFFFD600)
    val AccentOrange = Color(0xFFFB923C)

    fun getColorsForTier(tier: Int): Pair<Color, Color> {
        return when (tier) {
            1 -> Color(0xFFC084FC) to Color(0xFFA855F7) // Violet (as in screenshot '1' on left)
            2 -> Color(0xFF38BDF8) to Color(0xFF0284C7) // Cyan / Light Blue
            3 -> Color(0xFFFBBF24) to Color(0xFFF59E0B) // Amber / Golden Orange
            4 -> Color(0xFFF87171) to Color(0xFFDC2626) // Coral / Crimson Red
            5 -> Color(0xFF84CC16) to Color(0xFF65A30D) // Bright Lime Green
            6 -> Color(0xFFFB923C) to Color(0xFFEA580C) // Bright Orange
            7 -> Color(0xFFF472B6) to Color(0xFFDB2777) // Lavender Pink
            8 -> Color(0xFF6366F1) to Color(0xFF4338CA) // Electric Indigo
            9 -> Color(0xFF10B981) to Color(0xFF059669) // Emerald Mint
            10 -> Color(0xFF06B6D4) to Color(0xFF0891B2) // Vivid Teal
            11 -> Color(0xFFFF2A2A) to Color(0xFFB91C1C) // Fiery Crimson
            12 -> Color(0xFFFFD700) to Color(0xFFD97706) // Golden Sun
            else -> Color(0xFFEC4899) to Color(0xFF8B5CF6) // Prismatic Ultraviolet
        }
    }

    fun getBrushForTier(tier: Int): Brush {
        val (c1, c2) = getColorsForTier(tier)
        return Brush.verticalGradient(listOf(c1, c2))
    }

    fun getTextColorForTier(tier: Int): Color {
        return Color.White
    }
}
