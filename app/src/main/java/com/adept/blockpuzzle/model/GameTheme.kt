package com.adept.blockpuzzle.model

import androidx.compose.ui.graphics.Color

data class GameTheme(
    val id: String,
    val name: String,
    val price: Int, // 0 for default
    val description: String,
    val iconEmoji: String,
    val bgTop: Color,
    val bgMid: Color,
    val bgBottom: Color,
    val boardBackdrop: Color,
    val boardBorder: Color,
    val emptyCellFill: Color,
    val emptyCellBorder: Color,
    val accentColor: Color
) {
    companion object {
        val CYBER_NEON = GameTheme(
            id = "cyber_neon",
            name = "Cyber Neon",
            price = 0,
            description = "Sleek radiant sky blue bokeh with electric cyber board.",
            iconEmoji = "🌌",
            bgTop = Color(0xFF90D5FF),
            bgMid = Color(0xFFC3EAFF),
            bgBottom = Color(0xFFE2F4FF),
            boardBackdrop = Color(0xFF222C42),
            boardBorder = Color(0xFF324160),
            emptyCellFill = Color(0xFF1B2338),
            emptyCellBorder = Color(0xFF2B3856),
            accentColor = Color(0xFF38BDF8)
        )

        val COSMIC_AMETHYST = GameTheme(
            id = "cosmic_amethyst",
            name = "Cosmic Amethyst",
            price = 350,
            description = "Mystic deep purple galaxy nebula with glowing stardust crystals.",
            iconEmoji = "🔮",
            bgTop = Color(0xFFE9D5FF),
            bgMid = Color(0xFFD8B4FE),
            bgBottom = Color(0xFFC084FC),
            boardBackdrop = Color(0xFF241538),
            boardBorder = Color(0xFF7E22CE),
            emptyCellFill = Color(0xFF1B0E2B),
            emptyCellBorder = Color(0xFF581C87),
            accentColor = Color(0xFFC084FC)
        )

        val GOLDEN_HONEYCOMB = GameTheme(
            id = "golden_honeycomb",
            name = "Golden Honeycomb",
            price = 500,
            description = "Rich amber honeycomb grid with regal warm sunburst accents.",
            iconEmoji = "🍯",
            bgTop = Color(0xFFFEF3C7),
            bgMid = Color(0xFFFDE68A),
            bgBottom = Color(0xFFFCD34D),
            boardBackdrop = Color(0xFF38260F),
            boardBorder = Color(0xFFD97706),
            emptyCellFill = Color(0xFF291B09),
            emptyCellBorder = Color(0xFF78350F),
            accentColor = Color(0xFFF59E0B)
        )

        val EMERALD_FOREST = GameTheme(
            id = "emerald_forest",
            name = "Emerald Forest",
            price = 400,
            description = "Lush jade crystal gemstone styling with refreshing mint highlights.",
            iconEmoji = "🌲",
            bgTop = Color(0xFFA7F3D0),
            bgMid = Color(0xFF6EE7B7),
            bgBottom = Color(0xFF34D399),
            boardBackdrop = Color(0xFF112E23),
            boardBorder = Color(0xFF059669),
            emptyCellFill = Color(0xFF0C2119),
            emptyCellBorder = Color(0xFF047857),
            accentColor = Color(0xFF10B981)
        )

        val ALL_THEMES = listOf(CYBER_NEON, COSMIC_AMETHYST, GOLDEN_HONEYCOMB, EMERALD_FOREST)

        fun fromId(id: String): GameTheme {
            return ALL_THEMES.firstOrNull { it.id == id } ?: CYBER_NEON
        }
    }
}
