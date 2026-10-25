package com.adept.blockpuzzle.model

enum class BoosterType {
    HAMMER,
    SHUFFLE,
    RAINBOW
}

data class StoreBoosterItem(
    val type: BoosterType,
    val name: String,
    val iconEmoji: String,
    val description: String,
    val price: Int,
    val bundleCount: Int = 1
)

object StoreCatalog {
    val BOOSTERS = listOf(
        StoreBoosterItem(
            type = BoosterType.HAMMER,
            name = "Tile Hammer",
            iconEmoji = "🔨",
            description = "Smash and obliterate any single tile blocking your board.",
            price = 150,
            bundleCount = 1
        ),
        StoreBoosterItem(
            type = BoosterType.SHUFFLE,
            name = "Tray Shuffle",
            iconEmoji = "🔄",
            description = "Reroll all 3 upcoming pieces in your dock for brand new shapes.",
            price = 100,
            bundleCount = 1
        ),
        StoreBoosterItem(
            type = BoosterType.RAINBOW,
            name = "Rainbow Hex",
            iconEmoji = "🌈",
            description = "Spawn a universal 1x1 mini hex in tray to fit any narrow hole.",
            price = 200,
            bundleCount = 1
        )
    )
}
