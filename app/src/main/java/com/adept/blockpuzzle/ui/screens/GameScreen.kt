package com.adept.blockpuzzle.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.adept.blockpuzzle.game.HexPieceGenerator
import com.adept.blockpuzzle.model.GameStatus
import com.adept.blockpuzzle.model.HexCoord
import com.adept.blockpuzzle.model.HexPiece
import com.adept.blockpuzzle.ui.components.DragOverlay
import com.adept.blockpuzzle.ui.components.GameOverDialog
import com.adept.blockpuzzle.ui.components.GameHeader
import com.adept.blockpuzzle.ui.components.HexBoardCanvas
import com.adept.blockpuzzle.ui.components.LevelCompleteDialog
import com.adept.blockpuzzle.ui.components.NextBlocksTray
import com.adept.blockpuzzle.ui.components.PauseDialog
import com.adept.blockpuzzle.ui.components.SettingsDialog
import com.adept.blockpuzzle.ui.components.SkyHexBokehBackground
import com.adept.blockpuzzle.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onQuitToMenu: () -> Unit,
    onOpenStore: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Drag-and-drop state
    var draggedPiece by remember { mutableStateOf<HexPiece?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var boardCenterInScreen by remember { mutableStateOf(Offset.Zero) }
    var boardCenterLocal by remember { mutableStateOf(Offset.Zero) }
    var currentHexRadius by remember { mutableFloatStateOf(32f) }

    val density = LocalDensity.current
    val liftOffsetPx = remember(density) { with(density) { 72.dp.toPx() } }

    // Handle Android system back button
    BackHandler {
        if (uiState.isHammerMode) {
            viewModel.cancelHammerMode()
        } else if (uiState.isSettingsOpen) {
            viewModel.closeSettings()
        } else if (uiState.gameStatus == GameStatus.PLAYING) {
            viewModel.pauseGame()
        } else if (uiState.gameStatus == GameStatus.PAUSED) {
            viewModel.resumeGame()
        } else {
            onQuitToMenu()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("game_screen")
    ) {
        // 1. Wallpaper with dynamic theme
        SkyHexBokehBackground(theme = uiState.activeTheme)

        Column(modifier = Modifier.fillMaxSize()) {
            // 2. Top Header Bar (Score bar, Coins, and pause button)
            GameHeader(
                score = uiState.score,
                highScore = uiState.highScore,
                gameMode = uiState.gameMode,
                levelNumber = uiState.currentLevelNumber,
                levelTarget = uiState.levelTarget,
                highestTier = uiState.highestTierOnBoard,
                linesCleared = uiState.linesClearedTotal,
                comboStreak = uiState.comboStreak,
                comboMultiplier = uiState.activeComboMultiplier,
                coins = uiState.coins,
                onPauseClick = { viewModel.pauseGame() }
            )

            // 3. Hexagonal Game Board Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(uiState.selectedPiece, uiState.isHammerMode, boardCenterLocal, currentHexRadius) {
                        detectTapGestures { tapOffset ->
                            if (uiState.isHammerMode) {
                                val coord = HexCoord.fromScreenOffset(tapOffset, boardCenterLocal, currentHexRadius)
                                if (coord.distanceToCenter() <= HexCoord.GRID_RADIUS) {
                                    viewModel.useHammerOnCell(coord, boardCenterLocal, currentHexRadius)
                                }
                            } else {
                                val selected = uiState.selectedPiece
                                if (selected != null) {
                                    val coord = HexCoord.fromScreenOffset(tapOffset, boardCenterLocal, currentHexRadius)
                                    if (coord.distanceToCenter() <= HexCoord.GRID_RADIUS) {
                                        viewModel.placeSelectedPiece(coord, boardCenterLocal, currentHexRadius)
                                    }
                                }
                            }
                        }
                    }
            ) {
                HexBoardCanvas(
                    board = uiState.board,
                    hoverPiece = draggedPiece ?: uiState.selectedPiece,
                    hoverAnchor = uiState.hoverAnchor,
                    isHoverValid = uiState.isHoverValid,
                    particles = viewModel.particleSystem.getParticles(),
                    floatingTexts = viewModel.particleSystem.getFloatingTexts(),
                    brightness = uiState.brightness,
                    activeTheme = uiState.activeTheme,
                    isHammerMode = uiState.isHammerMode,
                    onBoardMetricsChanged = { centerRoot, centerLocal, radius ->
                        boardCenterInScreen = centerRoot
                        boardCenterLocal = centerLocal
                        currentHexRadius = radius
                    }
                )
            }

            // 4. Boosters Action Bar
            com.adept.blockpuzzle.ui.components.BoosterActionBar(
                hammersCount = uiState.hammersCount,
                shufflesCount = uiState.shufflesCount,
                rainbowCount = uiState.rainbowCount,
                coins = uiState.coins,
                isHammerMode = uiState.isHammerMode,
                onToggleHammer = { viewModel.toggleHammerMode() },
                onShuffle = { viewModel.useShuffle(boardCenterLocal) },
                onRainbowHex = { viewModel.useRainbowHex(boardCenterLocal) },
                onOpenStore = onOpenStore
            )

            // 5. Bottom Next Blocks Dock (Matching Panel 2 dock)
            NextBlocksTray(
                pieces = uiState.nextPieces,
                draggedPiece = draggedPiece,
                selectedPiece = uiState.selectedPiece,
                onSelectPiece = { piece -> viewModel.selectPiece(piece) },
                onDragStart = { piece, startTouchInRoot ->
                    draggedPiece = piece
                    dragPosition = startTouchInRoot
                    viewModel.selectPiece(piece)
                },
                onDragMove = { currentTouchInRoot ->
                    dragPosition = currentTouchInRoot
                    val piece = draggedPiece
                    if (piece != null && currentHexRadius > 0f) {
                        val candidatePos = Offset(currentTouchInRoot.x, currentTouchInRoot.y - liftOffsetPx)
                        val candidateCoord = HexCoord.fromScreenOffset(candidatePos, boardCenterInScreen, currentHexRadius)
                        if (candidateCoord.distanceToCenter() <= HexCoord.GRID_RADIUS) {
                            val isValid = HexPieceGenerator.canPlacePiece(piece, candidateCoord, uiState.board)
                            viewModel.setHover(piece, candidateCoord, isValid)
                        } else {
                            viewModel.setHover(piece, null, false)
                        }
                    }
                },
                onDragEnd = {
                    val piece = draggedPiece
                    val hoverAnchor = uiState.hoverAnchor
                    val isValid = uiState.isHoverValid

                    if (piece != null && hoverAnchor != null && isValid) {
                        viewModel.placeSelectedPiece(hoverAnchor, boardCenterLocal, currentHexRadius)
                    }

                    draggedPiece = null
                    viewModel.clearHover()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        // 5. Drag Overlay: draws the piece floating directly under the player's finger
        DragOverlay(
            draggedPiece = draggedPiece,
            dragPosition = dragPosition,
            hexRadius = currentHexRadius,
            liftOffsetPx = liftOffsetPx
        )

        // 6. Overlay Dialogs (Matching Panels 3, 4, 5, 6)
        if (uiState.gameStatus == GameStatus.PAUSED && !uiState.isSettingsOpen) {
            PauseDialog(
                onResume = { viewModel.resumeGame() },
                onRestart = { viewModel.restartCurrentGame() },
                onSettings = { viewModel.openSettings() },
                onQuit = onQuitToMenu
            )
        }

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

        if (uiState.gameStatus == GameStatus.LEVEL_COMPLETE) {
            LevelCompleteDialog(
                levelNumber = uiState.currentLevelNumber,
                score = uiState.score,
                stars = uiState.stars,
                onNextLevel = { viewModel.nextLevel() },
                onClose = onQuitToMenu
            )
        }

        if (uiState.gameStatus == GameStatus.GAME_OVER) {
            GameOverDialog(
                score = uiState.score,
                onRetry = { viewModel.restartCurrentGame() },
                onQuit = onQuitToMenu,
                onOpenStore = onOpenStore
            )
        }
    }
}
