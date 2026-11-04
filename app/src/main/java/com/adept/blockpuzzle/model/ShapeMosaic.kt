package com.adept.blockpuzzle.model

import androidx.compose.ui.graphics.Color

data class ShapeBlock(
    val index: Int,
    val coord: HexCoord,
    val label: String = ""
)

data class ShapeMosaic(
    val id: String,
    val name: String,
    val title: String,
    val totalBlocks: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val iconEmoji: String,
    val blocks: List<ShapeBlock>
) {
    /**
     * Returns the primary and secondary colors for this shape, adapting dynamically
     * to the player's active theme when rendering the Hex Shield.
     */
    fun getThemeColors(theme: GameTheme): Pair<Color, Color> {
        return if (id == "hex_shield") {
            when (theme.id) {
                "cosmic_amethyst" -> Color(0xFFC084FC) to Color(0xFF7E22CE)
                "golden_honeycomb" -> Color(0xFFF59E0B) to Color(0xFFD97706)
                "emerald_forest" -> Color(0xFF10B981) to Color(0xFF059669)
                else -> Color(0xFF38BDF8) to Color(0xFF0284C7) // Cyber Neon
            }
        } else {
            primaryColor to secondaryColor
        }
    }
}

data class ShapeLevelUpReward(
    val levelNumber: Int,
    val score: Int,
    val shape: ShapeMosaic,
    val previouslyFilledCount: Int,
    val newlyFilledIndex: Int,
    val isShapeFullyCompleted: Boolean,
    val coinsEarned: Int,
    val isInfiniteMode: Boolean
)

object ShapeMosaicCatalog {

    // 1. Hex Relic Shield (7 blocks: center + 6 outer rosette)
    val HEX_SHIELD = ShapeMosaic(
        id = "hex_shield",
        name = "Hex Shield",
        title = "Ancient Aegis of Harmony",
        totalBlocks = 7,
        primaryColor = Color(0xFF38BDF8), // Cyber Sky Blue (matches Cyber Neon theme)
        secondaryColor = Color(0xFF0284C7), // Electric Cyber Cyan (matches Cyber Neon theme)
        iconEmoji = "🛡️",
        blocks = listOf(
            ShapeBlock(0, HexCoord(0, 0), "Core"),
            ShapeBlock(1, HexCoord(0, -1), "Top"),
            ShapeBlock(2, HexCoord(1, -1), "Upper-Right"),
            ShapeBlock(3, HexCoord(1, 0), "Lower-Right"),
            ShapeBlock(4, HexCoord(0, 1), "Bottom"),
            ShapeBlock(5, HexCoord(-1, 1), "Lower-Left"),
            ShapeBlock(6, HexCoord(-1, 0), "Upper-Left")
        )
    )

    // 2. Cosmic Star (6 blocks: radiating star cluster)
    val COSMIC_STAR = ShapeMosaic(
        id = "cosmic_star",
        name = "Cosmic Star",
        title = "Luminous Astral Prism",
        totalBlocks = 6,
        primaryColor = Color(0xFFF59E0B), // Amber Gold
        secondaryColor = Color(0xFFFDE047), // Solar Yellow
        iconEmoji = "⭐",
        blocks = listOf(
            ShapeBlock(0, HexCoord(0, -1), "Zenith"),
            ShapeBlock(1, HexCoord(1, -1), "East-Star"),
            ShapeBlock(2, HexCoord(1, 0), "Dusk-Star"),
            ShapeBlock(3, HexCoord(0, 1), "Nadir"),
            ShapeBlock(4, HexCoord(-1, 1), "Dawn-Star"),
            ShapeBlock(5, HexCoord(-1, 0), "West-Star")
        )
    )

    // 3. Crystal Crown (6 blocks: regal crown)
    val CRYSTAL_CROWN = ShapeMosaic(
        id = "crystal_crown",
        name = "Crystal Crown",
        title = "Coronet of the Sovereign",
        totalBlocks = 6,
        primaryColor = Color(0xFF8B5CF6), // Royal Purple
        secondaryColor = Color(0xFFEC4899), // Hot Pink
        iconEmoji = "👑",
        blocks = listOf(
            ShapeBlock(0, HexCoord(-1, 1), "Base Left"),
            ShapeBlock(1, HexCoord(0, 1), "Base Center"),
            ShapeBlock(2, HexCoord(1, 0), "Base Right"),
            ShapeBlock(3, HexCoord(-1, 0), "Left Spire"),
            ShapeBlock(4, HexCoord(0, 0), "Crown Gem"),
            ShapeBlock(5, HexCoord(0, -1), "High Peak")
        )
    )

    // 4. Radiant Diamond (5 blocks: classic brilliant cut gem)
    val RADIANT_DIAMOND = ShapeMosaic(
        id = "radiant_diamond",
        name = "Radiant Diamond",
        title = "Eternal Faceted Jewel",
        totalBlocks = 5,
        primaryColor = Color(0xFF38BDF8), // Sky Blue
        secondaryColor = Color(0xFF6366F1), // Royal Indigo
        iconEmoji = "💎",
        blocks = listOf(
            ShapeBlock(0, HexCoord(0, 0), "Heart"),
            ShapeBlock(1, HexCoord(0, -1), "Top Facet"),
            ShapeBlock(2, HexCoord(1, 0), "Right Facet"),
            ShapeBlock(3, HexCoord(0, 1), "Bottom Facet"),
            ShapeBlock(4, HexCoord(-1, 0), "Left Facet")
        )
    )

    // 5. Phoenix Heart (6 blocks: warm heart emblem)
    val PHOENIX_HEART = ShapeMosaic(
        id = "phoenix_heart",
        name = "Phoenix Heart",
        title = "Flame-Forged Core",
        totalBlocks = 6,
        primaryColor = Color(0xFFF43F5E), // Coral Crimson
        secondaryColor = Color(0xFFFB7185), // Soft Rose
        iconEmoji = "❤️",
        blocks = listOf(
            ShapeBlock(0, HexCoord(0, 0), "Heart Center"),
            ShapeBlock(1, HexCoord(-1, 0), "Left Lobe"),
            ShapeBlock(2, HexCoord(1, -1), "Right Lobe"),
            ShapeBlock(3, HexCoord(0, -1), "Cleft Peak"),
            ShapeBlock(4, HexCoord(-1, 1), "Left Wing"),
            ShapeBlock(5, HexCoord(0, 1), "Heart Point")
        )
    )

    // 6. Sacred Lotus (7 blocks: blossom petals)
    val SACRED_LOTUS = ShapeMosaic(
        id = "sacred_lotus",
        name = "Sacred Lotus",
        title = "Blossom of Serenity",
        totalBlocks = 7,
        primaryColor = Color(0xFFD946EF), // Fuchsia
        secondaryColor = Color(0xFF2DD4BF), // Aqua Teal
        iconEmoji = "🪷",
        blocks = listOf(
            ShapeBlock(0, HexCoord(0, 0), "Lotus Seed"),
            ShapeBlock(1, HexCoord(0, -1), "Top Petal"),
            ShapeBlock(2, HexCoord(1, -1), "Upper Petal"),
            ShapeBlock(3, HexCoord(1, 0), "Side Petal"),
            ShapeBlock(4, HexCoord(0, 1), "Water Base"),
            ShapeBlock(5, HexCoord(-1, 1), "Leaf Left"),
            ShapeBlock(6, HexCoord(-1, 0), "Outer Blossom")
        )
    )

    // 7. Solar Sunburst (7 blocks: celestial sun)
    val SOLAR_SUN = ShapeMosaic(
        id = "solar_sun",
        name = "Solar Sunburst",
        title = "Radiance of the Dawn",
        totalBlocks = 7,
        primaryColor = Color(0xFFF97316), // Orange
        secondaryColor = Color(0xFFFFD600), // Solar Gold
        iconEmoji = "☀️",
        blocks = listOf(
            ShapeBlock(0, HexCoord(0, 0), "Sun Core"),
            ShapeBlock(1, HexCoord(0, -1), "Noon Ray"),
            ShapeBlock(2, HexCoord(1, -1), "Afternoon Ray"),
            ShapeBlock(3, HexCoord(1, 0), "Sunset Ray"),
            ShapeBlock(4, HexCoord(0, 1), "Midnight Ray"),
            ShapeBlock(5, HexCoord(-1, 1), "Twilight Ray"),
            ShapeBlock(6, HexCoord(-1, 0), "Sunrise Ray")
        )
    )

    val allShapes: List<ShapeMosaic> = listOf(
        HEX_SHIELD,
        COSMIC_STAR,
        CRYSTAL_CROWN,
        RADIANT_DIAMOND,
        PHOENIX_HEART,
        SACRED_LOTUS,
        SOLAR_SUN
    )

    fun getShape(index: Int): ShapeMosaic {
        return allShapes[index.mod(allShapes.size)]
    }
}
