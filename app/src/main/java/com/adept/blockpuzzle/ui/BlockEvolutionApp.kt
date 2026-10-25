package com.adept.blockpuzzle.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adept.blockpuzzle.ui.components.SettingsDialog
import com.adept.blockpuzzle.ui.screens.ChallengesScreen
import com.adept.blockpuzzle.ui.screens.GameScreen
import com.adept.blockpuzzle.ui.screens.MainMenuScreen
import com.adept.blockpuzzle.ui.screens.StoreScreen
import com.adept.blockpuzzle.ui.theme.HexColors
import com.adept.blockpuzzle.ui.viewmodel.GameViewModel

enum class ScreenState {
    MAIN_MENU,
    GAME,
    CHALLENGES,
    STORE
}

@Composable
fun BlockEvolutionApp(
    viewModel: GameViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf(ScreenState.MAIN_MENU) }
    var previousScreen by remember { mutableStateOf(ScreenState.MAIN_MENU) }
    val uiState by viewModel.uiState.collectAsState()

    fun navigateToStore() {
        previousScreen = currentScreen
        currentScreen = ScreenState.STORE
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (currentScreen) {
            ScreenState.MAIN_MENU -> {
                MainMenuScreen(
                    soundEnabled = uiState.soundFxEnabled,
                    dailyStreak = uiState.dailyStreak,
                    isDailyCompleted = uiState.isDailyCompletedToday,
                    coins = uiState.coins,
                    activeTheme = uiState.activeTheme,
                    onPlayGame = {
                        viewModel.startNewClassicGame()
                        currentScreen = ScreenState.GAME
                    },
                    onPlayDaily = {
                        viewModel.startDailyChallenge()
                        currentScreen = ScreenState.GAME
                    },
                    onOpenChallenges = {
                        currentScreen = ScreenState.CHALLENGES
                    },
                    onOpenStore = {
                        navigateToStore()
                    },
                    onOpenSettings = {
                        viewModel.openSettings()
                    },
                    onToggleSound = {
                        viewModel.toggleSoundFx()
                    }
                )

                if (uiState.isSettingsOpen) {
                    SettingsDialog(
                        musicEnabled = uiState.musicEnabled,
                        soundFxEnabled = uiState.soundFxEnabled,
                        brightness = uiState.brightness,
                        onToggleMusic = { viewModel.toggleMusic() },
                        onToggleSoundFx = { viewModel.toggleSoundFx() },
                        onBrightnessChange = { b -> viewModel.setBrightness(b) },
                        onClose = { viewModel.closeSettings() }
                    )
                }
            }

            ScreenState.CHALLENGES -> {
                BackHandler {
                    currentScreen = ScreenState.MAIN_MENU
                }
                ChallengesScreen(
                    unlockedLevels = uiState.unlockedLevels,
                    levelStarsMap = uiState.levelStarsMap,
                    levelScoresMap = uiState.levelScoresMap,
                    isDailyCompleted = uiState.isDailyCompletedToday,
                    dailyStreak = uiState.dailyStreak,
                    coins = uiState.coins,
                    activeTheme = uiState.activeTheme,
                    onSelectLevel = { levelNum ->
                        viewModel.startLevel(levelNum)
                        currentScreen = ScreenState.GAME
                    },
                    onPlayDaily = {
                        viewModel.startDailyChallenge()
                        currentScreen = ScreenState.GAME
                    },
                    onOpenStore = {
                        navigateToStore()
                    },
                    onBack = {
                        currentScreen = ScreenState.MAIN_MENU
                    }
                )
            }

            ScreenState.GAME -> {
                GameScreen(
                    viewModel = viewModel,
                    onQuitToMenu = {
                        currentScreen = ScreenState.MAIN_MENU
                    },
                    onOpenStore = {
                        navigateToStore()
                    }
                )
            }

            ScreenState.STORE -> {
                BackHandler {
                    currentScreen = previousScreen
                }
                StoreScreen(
                    coins = uiState.coins,
                    hammersCount = uiState.hammersCount,
                    shufflesCount = uiState.shufflesCount,
                    rainbowCount = uiState.rainbowCount,
                    ownedThemes = uiState.ownedThemes,
                    activeThemeId = uiState.activeThemeId,
                    canClaimDailyReward = uiState.canClaimDailyReward,
                    canClaimAdReward = uiState.canClaimAdReward,
                    rewardNotification = uiState.rewardNotification,
                    onBuyBooster = { type, cost -> viewModel.buyBooster(type, cost) },
                    onBuyTheme = { theme -> viewModel.buyTheme(theme) },
                    onEquipTheme = { themeId -> viewModel.equipTheme(themeId) },
                    onClaimDailyReward = { viewModel.claimDailyReward() },
                    onClaimAdReward = { viewModel.claimFreeAdReward() },
                    onDismissNotification = { viewModel.dismissRewardNotification() },
                    onBack = {
                        currentScreen = previousScreen
                    }
                )
            }
        }
    }
}
