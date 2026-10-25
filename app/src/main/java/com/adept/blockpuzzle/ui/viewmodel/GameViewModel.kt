package com.adept.blockpuzzle.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adept.blockpuzzle.data.db.AppDatabase
import com.adept.blockpuzzle.data.model.GameRecordEntity
import com.adept.blockpuzzle.data.model.GameSettingEntity
import com.adept.blockpuzzle.data.model.UserEconomyEntity
import com.adept.blockpuzzle.data.repository.GameRepository
import com.adept.blockpuzzle.game.DailyChallengeManager
import com.adept.blockpuzzle.game.GameEngine
import com.adept.blockpuzzle.game.HexPieceGenerator
import com.adept.blockpuzzle.game.LevelCatalog
import com.adept.blockpuzzle.game.ParticleSystem
import com.adept.blockpuzzle.game.SoundManager
import com.adept.blockpuzzle.model.BoosterType
import com.adept.blockpuzzle.model.CellData
import com.adept.blockpuzzle.model.GameMode
import com.adept.blockpuzzle.model.GameStatus
import com.adept.blockpuzzle.model.GameTheme
import com.adept.blockpuzzle.model.HexCoord
import com.adept.blockpuzzle.model.HexPiece
import com.adept.blockpuzzle.model.LevelTarget
import com.adept.blockpuzzle.ui.theme.HexColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GameUiState(
    val gameMode: GameMode = GameMode.CLASSIC,
    val currentLevelNumber: Int = 1,
    val levelTarget: LevelTarget? = null,
    val score: Int = 0,
    val highScore: Int = 0,
    val stars: Int = 0,
    val linesClearedTotal: Int = 0,
    val highestTierOnBoard: Int = 1,
    val board: Map<HexCoord, CellData> = emptyMap(),
    val nextPieces: List<HexPiece> = emptyList(),
    val selectedPiece: HexPiece? = null,
    val hoverAnchor: HexCoord? = null,
    val isHoverValid: Boolean = false,
    val gameStatus: GameStatus = GameStatus.PLAYING,
    val isSettingsOpen: Boolean = false,
    val musicEnabled: Boolean = true,
    val soundFxEnabled: Boolean = true,
    val brightness: Float = 0.85f,
    val comboStreak: Int = 0,
    val activeComboMultiplier: Int = 1,
    val maxComboStreak: Int = 0,
    val unlockedLevels: Set<Int> = setOf(1),
    val levelStarsMap: Map<Int, Int> = emptyMap(),
    val levelScoresMap: Map<Int, Int> = emptyMap(),
    val isDailyCompletedToday: Boolean = false,
    val dailyStreak: Int = 0,
    val todayDateKey: String = "",
    val dailyBestScore: Int = 0,
    // Economy & Store
    val coins: Int = 500,
    val hammersCount: Int = 2,
    val shufflesCount: Int = 2,
    val rainbowCount: Int = 1,
    val ownedThemes: Set<String> = setOf("cyber_neon"),
    val activeThemeId: String = "cyber_neon",
    val activeTheme: GameTheme = GameTheme.CYBER_NEON,
    val isHammerMode: Boolean = false,
    val lastDailyRewardClaimTime: Long = 0L,
    val lastAdRewardClaimTime: Long = 0L,
    val canClaimDailyReward: Boolean = true,
    val canClaimAdReward: Boolean = true,
    val rewardNotification: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    val soundManager = SoundManager()
    val particleSystem = ParticleSystem()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GameRepository(database.gameDao())

        // Load settings and records
        viewModelScope.launch {
            repository.settings.collect { setting ->
                soundManager.musicEnabled = setting.musicEnabled
                soundManager.soundFxEnabled = setting.soundFxEnabled
                _uiState.value = _uiState.value.copy(
                    musicEnabled = setting.musicEnabled,
                    soundFxEnabled = setting.soundFxEnabled,
                    brightness = setting.brightness
                )
            }
        }

        viewModelScope.launch {
            repository.allRecords.collect { records ->
                val unlocked = mutableSetOf(1)
                val starsMap = mutableMapOf<Int, Int>()
                val scoresMap = mutableMapOf<Int, Int>()
                var classicHigh = 0

                for (rec in records) {
                    if (rec.id == "classic") {
                        classicHigh = rec.highScore
                    } else if (rec.id.startsWith("level_")) {
                        val lvlNum = rec.id.removePrefix("level_").toIntOrNull()
                        if (lvlNum != null) {
                            if (rec.isUnlocked) unlocked.add(lvlNum)
                            starsMap[lvlNum] = rec.stars
                            scoresMap[lvlNum] = rec.highScore
                        }
                    }
                }

                _uiState.value = _uiState.value.copy(
                    highScore = if (_uiState.value.gameMode == GameMode.CLASSIC) classicHigh else (_uiState.value.highScore),
                    unlockedLevels = unlocked,
                    levelStarsMap = starsMap,
                    levelScoresMap = scoresMap
                )
            }
        }

        // Observe Daily Challenge state
        val todayKey = DailyChallengeManager.getTodayDateKey()
        _uiState.value = _uiState.value.copy(todayDateKey = todayKey)

        viewModelScope.launch {
            repository.getDailyRecord(todayKey).collect { record ->
                if (record != null && record.isCompleted) {
                    _uiState.value = _uiState.value.copy(
                        isDailyCompletedToday = true,
                        dailyStreak = record.streakCount,
                        dailyBestScore = record.score
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getLatestDailyRecord().collect { latest ->
                if (!_uiState.value.isDailyCompletedToday) {
                    val currentStreak = if (latest != null && latest.dateKey == DailyChallengeManager.getYesterdayDateKey()) {
                        latest.streakCount
                    } else if (latest != null && latest.dateKey == todayKey && latest.isCompleted) {
                        latest.streakCount
                    } else {
                        0
                    }
                    _uiState.value = _uiState.value.copy(dailyStreak = currentStreak)
                }
            }
        }

        // Observe Economy & Store
        viewModelScope.launch {
            repository.economy.collect { eco ->
                val owned = eco.ownedThemes.split(",").filter { it.isNotBlank() }.toSet().ifEmpty { setOf("cyber_neon") }
                val activeThemeObj = GameTheme.fromId(eco.activeTheme)
                val now = System.currentTimeMillis()
                val canDaily = (now - eco.lastDailyRewardClaimTime) >= (20 * 3600 * 1000L) || eco.lastDailyRewardClaimTime == 0L
                val canAd = (now - eco.lastAdRewardClaimTime) >= (5 * 60 * 1000L) || eco.lastAdRewardClaimTime == 0L

                _uiState.value = _uiState.value.copy(
                    coins = eco.coins,
                    hammersCount = eco.hammersCount,
                    shufflesCount = eco.shufflesCount,
                    rainbowCount = eco.rainbowCount,
                    ownedThemes = owned,
                    activeThemeId = eco.activeTheme,
                    activeTheme = activeThemeObj,
                    lastDailyRewardClaimTime = eco.lastDailyRewardClaimTime,
                    lastAdRewardClaimTime = eco.lastAdRewardClaimTime,
                    canClaimDailyReward = canDaily,
                    canClaimAdReward = canAd
                )
            }
        }

        // Particle update loop
        viewModelScope.launch {
            var lastTime = System.nanoTime()
            while (isActive) {
                delay(16)
                val now = System.nanoTime()
                val dt = ((now - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastTime = now
                particleSystem.update(dt)
            }
        }

        startNewClassicGame()
    }

    fun startNewClassicGame() {
        val freshPieces = HexPieceGenerator.generatePieces(emptyMap(), 1, 3)
        _uiState.value = _uiState.value.copy(
            gameMode = GameMode.CLASSIC,
            currentLevelNumber = 1,
            levelTarget = null,
            score = 0,
            stars = 0,
            linesClearedTotal = 0,
            comboStreak = 0,
            activeComboMultiplier = 1,
            highestTierOnBoard = 1,
            board = emptyMap(),
            nextPieces = freshPieces,
            selectedPiece = null,
            hoverAnchor = null,
            isHoverValid = false,
            gameStatus = GameStatus.PLAYING
        )
        particleSystem.clear()
        if (_uiState.value.musicEnabled) {
            soundManager.startMusic()
        }
    }

    fun startLevel(levelNumber: Int) {
        val target = LevelCatalog.getLevel(levelNumber)
        val initialBoard = target.initialBoard
        val freshPieces = HexPieceGenerator.generatePieces(initialBoard, 1, 3)
        val bestScore = _uiState.value.levelScoresMap[levelNumber] ?: 0

        _uiState.value = _uiState.value.copy(
            gameMode = GameMode.CHALLENGE,
            currentLevelNumber = levelNumber,
            levelTarget = target,
            score = 0,
            highScore = bestScore,
            stars = 0,
            linesClearedTotal = 0,
            comboStreak = 0,
            activeComboMultiplier = 1,
            highestTierOnBoard = 1,
            board = initialBoard,
            nextPieces = freshPieces,
            selectedPiece = null,
            hoverAnchor = null,
            isHoverValid = false,
            gameStatus = GameStatus.PLAYING
        )
        particleSystem.clear()
        if (_uiState.value.musicEnabled) {
            soundManager.startMusic()
        }
    }

    fun startDailyChallenge() {
        val todayKey = DailyChallengeManager.getTodayDateKey()
        val target = DailyChallengeManager.getDailyChallenge(todayKey)
        val freshPieces = HexPieceGenerator.generatePieces(target.initialBoard, 1, 3)

        _uiState.value = _uiState.value.copy(
            gameMode = GameMode.DAILY_CHALLENGE,
            currentLevelNumber = 9999,
            levelTarget = target,
            score = 0,
            highScore = _uiState.value.dailyBestScore,
            stars = 0,
            linesClearedTotal = 0,
            comboStreak = 0,
            activeComboMultiplier = 1,
            highestTierOnBoard = 1,
            board = target.initialBoard,
            nextPieces = freshPieces,
            selectedPiece = null,
            hoverAnchor = null,
            isHoverValid = false,
            gameStatus = GameStatus.PLAYING
        )
        particleSystem.clear()
        if (_uiState.value.musicEnabled) {
            soundManager.startMusic()
        }
    }

    fun selectPiece(piece: HexPiece?) {
        _uiState.value = _uiState.value.copy(selectedPiece = piece)
        if (piece != null) {
            soundManager.playPickup()
            performHaptic(HapticType.LIGHT)
        }
    }

    fun setHover(piece: HexPiece?, anchor: HexCoord?, isValid: Boolean) {
        _uiState.value = _uiState.value.copy(
            selectedPiece = piece,
            hoverAnchor = anchor,
            isHoverValid = isValid
        )
    }

    fun clearHover() {
        _uiState.value = _uiState.value.copy(
            hoverAnchor = null,
            isHoverValid = false
        )
    }

    fun setHoverAnchor(anchor: HexCoord?, boardCenter: Offset, hexRadius: Float) {
        val piece = _uiState.value.selectedPiece
        if (piece == null || anchor == null) {
            _uiState.value = _uiState.value.copy(hoverAnchor = null, isHoverValid = false)
            return
        }

        val isValid = HexPieceGenerator.canPlacePiece(piece, anchor, _uiState.value.board)
        _uiState.value = _uiState.value.copy(hoverAnchor = anchor, isHoverValid = isValid)
    }

    fun placeSelectedPiece(anchor: HexCoord, boardCenter: Offset, hexRadius: Float) {
        val piece = _uiState.value.selectedPiece ?: return
        val currentBoard = _uiState.value.board
        if (!HexPieceGenerator.canPlacePiece(piece, anchor, currentBoard)) {
            performHaptic(HapticType.REJECT)
            return
        }

        soundManager.playPlace()
        performHaptic(HapticType.MEDIUM)

        val result = GameEngine.placePiece(
            piece = piece,
            anchor = anchor,
            currentBoard = currentBoard,
            currentScore = _uiState.value.score,
            highestTierOnBoard = _uiState.value.highestTierOnBoard,
            totalLinesCleared = _uiState.value.linesClearedTotal,
            remainingPiecesInTray = _uiState.value.nextPieces,
            currentComboStreak = _uiState.value.comboStreak,
            levelTarget = _uiState.value.levelTarget
        )

        // Spawn animations & SFX
        val anchorScreenPos = anchor.toScreenOffset(hexRadius, boardCenter)
        particleSystem.spawnMagicFountain(anchorScreenPos, count = if (result.newComboStreak >= 2) 35 else 20)

        // Combo celebration
        if (result.newComboStreak >= 2) {
            soundManager.playCombo(result.newComboStreak)
            performHaptic(HapticType.VICTORY)
            val comboText = when {
                result.newComboStreak >= 5 -> "ULTRA COMBO x${result.comboMultiplier}! 💥💥"
                result.newComboStreak == 4 -> "MEGA COMBO x${result.comboMultiplier}! 🔥🔥🔥"
                result.newComboStreak == 3 -> "SUPER COMBO x${result.comboMultiplier}! 🔥🔥"
                else -> "COMBO x${result.comboMultiplier}! 🔥"
            }
            val comboColor = when {
                result.newComboStreak >= 4 -> Color(0xFFFF1744)
                result.newComboStreak == 3 -> Color(0xFFFFAB00)
                else -> Color(0xFF00E5FF)
            }
            particleSystem.spawnFloatingText(
                text = comboText,
                position = Offset(anchorScreenPos.x, anchorScreenPos.y - 75f),
                color = comboColor,
                sizeSp = 28f
            )
        }

        // Floating points popup
        particleSystem.spawnFloatingText(
            text = "+${result.pointsScored}" + if (result.comboMultiplier > 1) " (x${result.comboMultiplier})" else "",
            position = anchorScreenPos,
            color = HexColors.AccentYellow
        )

        // If evolutions occurred
        if (result.evolvedCoords.isNotEmpty()) {
            for ((coord, tier) in result.evolvedCoords) {
                soundManager.playMerge(tier)
                performHaptic(HapticType.HEAVY)
                val pos = coord.toScreenOffset(hexRadius, boardCenter)
                val (c1, _) = HexColors.getColorsForTier(tier)
                particleSystem.spawnBurst(pos, c1, count = 25)
                particleSystem.spawnMagicFountain(pos, count = 20)
                particleSystem.spawnFloatingText(
                    text = "EVOLVED TO $tier!",
                    position = Offset(pos.x, pos.y - 30f),
                    color = c1,
                    sizeSp = 24f
                )
            }
        }

        // If lines cleared
        if (result.linesClearedCount > 0) {
            soundManager.playLineClear(result.linesClearedCount)
            performHaptic(HapticType.HEAVY)
            for (coord in result.clearedCoords) {
                val pos = coord.toScreenOffset(hexRadius, boardCenter)
                particleSystem.spawnBurst(pos, HexColors.AccentGreenBright, count = 8)
            }
            val lineText = when (result.linesClearedCount) {
                1 -> "LINE CLEAR!"
                2 -> "DOUBLE LINE!"
                3 -> "TRIPLE BLAST!"
                else -> "HEXA COMBO!!"
            }
            particleSystem.spawnFloatingText(
                text = lineText,
                position = Offset(anchorScreenPos.x, anchorScreenPos.y - 60f),
                color = HexColors.AccentGreenBright,
                sizeSp = 26f
            )
        }

        // Calculate coins earned on this move
        val lineCoins = result.linesClearedCount * 15 * result.comboMultiplier
        val mergeCoins = result.evolvedCoords.size * 8 * result.comboMultiplier
        val moveCoins = lineCoins + mergeCoins
        if (moveCoins > 0) {
            particleSystem.spawnFloatingText(
                text = "+$moveCoins 🪙",
                position = Offset(anchorScreenPos.x, anchorScreenPos.y - 100f),
                color = Color(0xFFFFD700),
                sizeSp = 24f
            )
            particleSystem.spawnCoinSparkles(anchorScreenPos, 14)
            soundManager.playCoin()
            viewModelScope.launch {
                repository.addCoins(moveCoins)
            }
        }

        val updatedPieces = _uiState.value.nextPieces.filter { it.id != piece.id }.toMutableList()
        if (updatedPieces.isEmpty()) {
            // Generate 3 fresh pieces
            updatedPieces.addAll(
                HexPieceGenerator.generatePieces(
                    result.newBoard,
                    result.highestTierReached,
                    3
                )
            )
        }

        val newScore = _uiState.value.score + result.pointsScored
        val newHighScore = maxOf(_uiState.value.highScore, newScore)

        var newStatus = GameStatus.PLAYING
        var starsEarned = 0

        if (result.isLevelComplete && _uiState.value.levelTarget != null) {
            newStatus = GameStatus.LEVEL_COMPLETE
            starsEarned = LevelCatalog.calculateStars(
                _uiState.value.levelTarget!!,
                newScore,
                result.highestTierReached,
                _uiState.value.linesClearedTotal + result.linesClearedCount
            ).coerceAtLeast(1)
            soundManager.playWin()
            performHaptic(HapticType.VICTORY)
            particleSystem.spawnConfetti(boardCenter.x * 2f, boardCenter.y * 2f, count = 60)

            val levelCoins = starsEarned * 50
            particleSystem.spawnFloatingText(
                text = "+$levelCoins 🪙 VICTORY BONUS!",
                position = Offset(boardCenter.x, boardCenter.y - 110f),
                color = Color(0xFFFFD700),
                sizeSp = 26f
            )
            viewModelScope.launch {
                repository.addCoins(levelCoins)
            }

            if (_uiState.value.gameMode == GameMode.DAILY_CHALLENGE) {
                val todayKey = DailyChallengeManager.getTodayDateKey()
                val prevStreak = _uiState.value.dailyStreak
                val newStreak = DailyChallengeManager.computeNewStreak(
                    todayKey = todayKey,
                    lastCompletedKey = if (prevStreak > 0) DailyChallengeManager.getYesterdayDateKey() else null,
                    previousStreak = prevStreak
                )
                val dailyBonusCoins = 250 + (newStreak * 25)
                particleSystem.spawnFloatingText(
                    text = "+$dailyBonusCoins 🪙 DAILY REWARD!",
                    position = Offset(boardCenter.x, boardCenter.y - 150f),
                    color = Color(0xFFFFD700),
                    sizeSp = 26f
                )
                viewModelScope.launch {
                    repository.addCoins(dailyBonusCoins)
                    repository.saveDailyResult(
                        dateKey = todayKey,
                        score = newScore,
                        highestTier = result.highestTierReached,
                        streak = newStreak
                    )
                }
                _uiState.value = _uiState.value.copy(
                    isDailyCompletedToday = true,
                    dailyStreak = newStreak,
                    dailyBestScore = maxOf(_uiState.value.dailyBestScore, newScore)
                )
            } else {
                viewModelScope.launch {
                    repository.saveLevelResult(
                        levelId = _uiState.value.currentLevelNumber,
                        score = newScore,
                        stars = starsEarned,
                        highestTier = result.highestTierReached
                    )
                }
            }
        } else if (result.isGameOver) {
            newStatus = GameStatus.GAME_OVER
            soundManager.playGameOver()
            performHaptic(HapticType.REJECT)

            if (_uiState.value.gameMode == GameMode.CLASSIC) {
                viewModelScope.launch {
                    repository.saveClassicHighScore(newScore, result.highestTierReached)
                }
            }
        }

        _uiState.value = _uiState.value.copy(
            board = result.newBoard,
            score = newScore,
            highScore = newHighScore,
            highestTierOnBoard = result.highestTierReached,
            linesClearedTotal = _uiState.value.linesClearedTotal + result.linesClearedCount,
            comboStreak = result.newComboStreak,
            activeComboMultiplier = result.comboMultiplier,
            maxComboStreak = maxOf(_uiState.value.maxComboStreak, result.newComboStreak),
            nextPieces = updatedPieces,
            selectedPiece = null,
            hoverAnchor = null,
            isHoverValid = false,
            gameStatus = newStatus,
            stars = starsEarned
        )
    }

    fun pauseGame() {
        if (_uiState.value.gameStatus == GameStatus.PLAYING) {
            _uiState.value = _uiState.value.copy(gameStatus = GameStatus.PAUSED)
        }
    }

    fun resumeGame() {
        if (_uiState.value.gameStatus == GameStatus.PAUSED) {
            _uiState.value = _uiState.value.copy(gameStatus = GameStatus.PLAYING)
        }
    }

    fun restartCurrentGame() {
        when (_uiState.value.gameMode) {
            GameMode.CLASSIC -> startNewClassicGame()
            GameMode.DAILY_CHALLENGE -> startDailyChallenge()
            GameMode.CHALLENGE -> startLevel(_uiState.value.currentLevelNumber)
        }
    }

    fun nextLevel() {
        if (_uiState.value.gameMode == GameMode.DAILY_CHALLENGE) {
            startDailyChallenge()
        } else {
            // Infinite level progression
            startLevel(_uiState.value.currentLevelNumber + 1)
        }
    }

    fun openSettings() {
        _uiState.value = _uiState.value.copy(isSettingsOpen = true)
    }

    fun closeSettings() {
        _uiState.value = _uiState.value.copy(isSettingsOpen = false)
    }

    fun toggleMusic() {
        val next = !_uiState.value.musicEnabled
        soundManager.musicEnabled = next
        _uiState.value = _uiState.value.copy(musicEnabled = next)
        saveSettings()
    }

    fun toggleSoundFx() {
        val next = !_uiState.value.soundFxEnabled
        soundManager.soundFxEnabled = next
        _uiState.value = _uiState.value.copy(soundFxEnabled = next)
        saveSettings()
    }

    fun setBrightness(brightness: Float) {
        _uiState.value = _uiState.value.copy(brightness = brightness)
        saveSettings()
    }

    private fun saveSettings() {
        viewModelScope.launch {
            repository.updateSettings(
                music = _uiState.value.musicEnabled,
                soundFx = _uiState.value.soundFxEnabled,
                brightness = _uiState.value.brightness
            )
        }
    }

    // BOOSTER & STORE ACTIONS
    fun toggleHammerMode() {
        if (_uiState.value.isHammerMode) {
            _uiState.value = _uiState.value.copy(isHammerMode = false)
        } else {
            if (_uiState.value.hammersCount > 0) {
                _uiState.value = _uiState.value.copy(isHammerMode = true, selectedPiece = null)
                performHaptic(HapticType.LIGHT)
            } else {
                soundManager.playError()
                _uiState.value = _uiState.value.copy(rewardNotification = "No Hammers! Buy more in the Store 🔨")
            }
        }
    }

    fun cancelHammerMode() {
        _uiState.value = _uiState.value.copy(isHammerMode = false)
    }

    fun useHammerOnCell(coord: HexCoord, boardCenterLocal: Offset, hexRadius: Float) {
        if (!_uiState.value.isHammerMode) return
        val cell = _uiState.value.board[coord]
        if (cell != null && cell.value > 0) {
            val newBoard = _uiState.value.board.toMutableMap()
            newBoard.remove(coord)
            val cellScreenPos = coord.toScreenOffset(hexRadius, boardCenterLocal)
            particleSystem.spawnSmash(cellScreenPos, count = 28)
            soundManager.playHammer()
            performHaptic(HapticType.HEAVY)

            val newHammers = (_uiState.value.hammersCount - 1).coerceAtLeast(0)
            _uiState.value = _uiState.value.copy(
                board = newBoard,
                hammersCount = newHammers,
                isHammerMode = false
            )
            viewModelScope.launch {
                val currentEco = repository.economy.first()
                repository.saveEconomy(currentEco.copy(hammersCount = newHammers))
            }
        } else {
            soundManager.playError()
        }
    }

    fun useShuffle(center: Offset? = null) {
        if (_uiState.value.shufflesCount <= 0) {
            soundManager.playError()
            _uiState.value = _uiState.value.copy(rewardNotification = "No Shuffles! Buy more in the Store 🔄")
            return
        }
        val newPieces = HexPieceGenerator.generatePieces(
            _uiState.value.board,
            _uiState.value.highestTierOnBoard,
            3
        )
        soundManager.playShuffle()
        performHaptic(HapticType.MEDIUM)
        if (center != null) {
            particleSystem.spawnMagicFountain(center, count = 18)
        }
        val newShuffles = (_uiState.value.shufflesCount - 1).coerceAtLeast(0)
        _uiState.value = _uiState.value.copy(
            nextPieces = newPieces,
            selectedPiece = null,
            shufflesCount = newShuffles
        )
        viewModelScope.launch {
            val currentEco = repository.economy.first()
            repository.saveEconomy(currentEco.copy(shufflesCount = newShuffles))
        }
    }

    fun useRainbowHex(center: Offset? = null) {
        if (_uiState.value.rainbowCount <= 0) {
            soundManager.playError()
            _uiState.value = _uiState.value.copy(rewardNotification = "No Rainbow Hex! Buy more in the Store 🌈")
            return
        }
        val current = _uiState.value.nextPieces.toMutableList()
        val wildTier = 1.coerceAtLeast((_uiState.value.highestTierOnBoard - 1).coerceIn(1, 4))
        val wildPiece = HexPiece(
            coords = HexPiece.SHAPE_SINGLE,
            values = listOf(wildTier),
            baseValue = wildTier
        )
        if (current.isNotEmpty()) {
            current[0] = wildPiece
        } else {
            current.add(wildPiece)
        }
        soundManager.playCoin()
        performHaptic(HapticType.MEDIUM)
        if (center != null) {
            particleSystem.spawnMagicFountain(center, count = 20)
        }
        val newRainbow = (_uiState.value.rainbowCount - 1).coerceAtLeast(0)
        _uiState.value = _uiState.value.copy(
            nextPieces = current,
            rainbowCount = newRainbow
        )
        viewModelScope.launch {
            val currentEco = repository.economy.first()
            repository.saveEconomy(currentEco.copy(rainbowCount = newRainbow))
        }
    }

    fun buyBooster(type: BoosterType, cost: Int) {
        if (_uiState.value.coins < cost) {
            soundManager.playError()
            _uiState.value = _uiState.value.copy(rewardNotification = "Not enough coins! Need 🪙 $cost")
            return
        }
        soundManager.playBuy()
        performHaptic(HapticType.VICTORY)
        viewModelScope.launch {
            val currentEco = repository.economy.first()
            val newCoins = currentEco.coins - cost
            val updatedEco = when (type) {
                BoosterType.HAMMER -> currentEco.copy(coins = newCoins, hammersCount = currentEco.hammersCount + 1)
                BoosterType.SHUFFLE -> currentEco.copy(coins = newCoins, shufflesCount = currentEco.shufflesCount + 1)
                BoosterType.RAINBOW -> currentEco.copy(coins = newCoins, rainbowCount = currentEco.rainbowCount + 1)
            }
            repository.saveEconomy(updatedEco)
            _uiState.value = _uiState.value.copy(
                rewardNotification = "Purchased ${type.name.lowercase().replaceFirstChar { it.uppercase() }}! 🎉"
            )
        }
    }

    fun buyTheme(theme: GameTheme) {
        if (_uiState.value.coins < theme.price) {
            soundManager.playError()
            _uiState.value = _uiState.value.copy(rewardNotification = "Not enough coins for ${theme.name}! Need 🪙 ${theme.price}")
            return
        }
        soundManager.playBuy()
        performHaptic(HapticType.VICTORY)
        viewModelScope.launch {
            val currentEco = repository.economy.first()
            val owned = currentEco.ownedThemes.split(",").filter { it.isNotBlank() }.toMutableSet()
            owned.add(theme.id)
            val updatedEco = currentEco.copy(
                coins = currentEco.coins - theme.price,
                ownedThemes = owned.joinToString(","),
                activeTheme = theme.id
            )
            repository.saveEconomy(updatedEco)
            _uiState.value = _uiState.value.copy(
                rewardNotification = "Unlocked & Equipped ${theme.name}! 🎨"
            )
        }
    }

    fun equipTheme(themeId: String) {
        soundManager.playCoin()
        performHaptic(HapticType.LIGHT)
        viewModelScope.launch {
            val currentEco = repository.economy.first()
            repository.saveEconomy(currentEco.copy(activeTheme = themeId))
            _uiState.value = _uiState.value.copy(
                activeThemeId = themeId,
                activeTheme = GameTheme.fromId(themeId),
                rewardNotification = "Equipped ${GameTheme.fromId(themeId).name}!"
            )
        }
    }

    fun claimDailyReward() {
        val now = System.currentTimeMillis()
        soundManager.playWin()
        performHaptic(HapticType.VICTORY)
        viewModelScope.launch {
            val currentEco = repository.economy.first()
            val updatedEco = currentEco.copy(
                coins = currentEco.coins + 250,
                hammersCount = currentEco.hammersCount + 1,
                lastDailyRewardClaimTime = now
            )
            repository.saveEconomy(updatedEco)
            _uiState.value = _uiState.value.copy(
                rewardNotification = "Claimed Daily Mystery Gift: +250 🪙 & +1 🔨!"
            )
        }
    }

    fun claimFreeAdReward() {
        val now = System.currentTimeMillis()
        soundManager.playCoin()
        performHaptic(HapticType.VICTORY)
        viewModelScope.launch {
            val currentEco = repository.economy.first()
            val updatedEco = currentEco.copy(
                coins = currentEco.coins + 100,
                lastAdRewardClaimTime = now
            )
            repository.saveEconomy(updatedEco)
            _uiState.value = _uiState.value.copy(
                rewardNotification = "Lucky Chest Opened: +100 🪙 received! 🎉"
            )
        }
    }

    fun dismissRewardNotification() {
        _uiState.value = _uiState.value.copy(rewardNotification = null)
    }

    private fun performHaptic(type: HapticType) {
        try {
            vibrator?.let { vib ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = when (type) {
                        HapticType.LIGHT -> VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
                        HapticType.MEDIUM -> VibrationEffect.createOneShot(45, 180)
                        HapticType.HEAVY -> VibrationEffect.createOneShot(80, 255)
                        HapticType.VICTORY -> VibrationEffect.createWaveform(longArrayOf(0, 50, 60, 100), -1)
                        HapticType.REJECT -> VibrationEffect.createWaveform(longArrayOf(0, 40, 40, 40), -1)
                    }
                    vib.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(40)
                }
            }
        } catch (_: Exception) {}
    }

    enum class HapticType {
        LIGHT, MEDIUM, HEAVY, VICTORY, REJECT
    }
}
