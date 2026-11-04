package com.adept.blockpuzzle.game

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Calm, meditative sound manager synthesizing soothing wooden chime,
 * crystal bell, water drop, and singing bowl sound effects.
 *
 * Fully lifecycle-aware: background music pauses/stops immediately when the app
 * is backgrounded or closed, and resumes seamlessly when foregrounded.
 */
class SoundManager {
    var soundFxEnabled: Boolean = true
    var musicEnabled: Boolean = true
        set(value) {
            field = value
            if (value && !isPaused) {
                startMusic()
            } else {
                stopMusic()
            }
        }

    private val scope = CoroutineScope(Dispatchers.Default)
    private var musicJob: Job? = null
    private val sampleRate = 22050

    @Volatile
    private var isPaused: Boolean = false

    @Volatile
    private var activeMusicTrack: AudioTrack? = null

    /**
     * Called when the app enters background or is stopped/closed.
     * Halts background music immediately without toggling player preference.
     */
    fun pauseMusic() {
        isPaused = true
        stopMusic()
    }

    /**
     * Called when the app returns to foreground.
     * Resumes background music if the user's setting is enabled.
     */
    fun resumeMusic() {
        isPaused = false
        if (musicEnabled) {
            startMusic()
        }
    }

    fun release() {
        isPaused = true
        stopMusic()
    }

    /**
     * Soft water drop / glass bead ping when picking up a block
     */
    fun playPickup() {
        if (!soundFxEnabled) return
        scope.launch {
            playCalmChime(frequency = 440.0, durationMs = 45, volume = 0.22f, chimeDecay = 4.0)
        }
    }

    /**
     * Gentle wooden marimba / pebble tap when placing a block
     */
    fun playPlace() {
        if (!soundFxEnabled) return
        scope.launch {
            playWarmWoodTap(frequency = 260.0, durationMs = 50, volume = 0.26f)
        }
    }

    /**
     * Calming crystal bell arpeggio when blocks merge
     */
    fun playMerge(tier: Int = 1) {
        if (!soundFxEnabled) return
        scope.launch {
            val pentatonicScale = listOf(261.63, 293.66, 329.63, 392.00, 440.00, 523.25, 587.33, 659.25, 783.99, 880.00)
            val baseIdx = (tier % pentatonicScale.size).coerceIn(0, pentatonicScale.size - 3)
            val note1 = pentatonicScale[baseIdx]
            val note2 = pentatonicScale[baseIdx + 1]
            val note3 = pentatonicScale[baseIdx + 2]

            playCalmChime(frequency = note1, durationMs = 90, volume = 0.28f)
            delay(50)
            playCalmChime(frequency = note2, durationMs = 110, volume = 0.30f)
            delay(55)
            playCalmChime(frequency = note3, durationMs = 150, volume = 0.32f, chimeDecay = 2.5)
        }
    }

    /**
     * Tranquil ascending Tibetan bell / wind chime tones for combos
     */
    fun playCombo(streak: Int) {
        if (!soundFxEnabled) return
        scope.launch {
            val comboNotes = listOf(392.00, 440.00, 523.25, 587.33, 659.25, 783.99, 880.00, 1046.50)
            val startIdx = (streak.coerceIn(1, 6) - 1)
            val note1 = comboNotes[startIdx % comboNotes.size]
            val note2 = comboNotes[(startIdx + 2) % comboNotes.size]
            val note3 = comboNotes[(startIdx + 4) % comboNotes.size]

            playCalmChime(frequency = note1, durationMs = 80, volume = 0.26f)
            delay(60)
            playCalmChime(frequency = note2, durationMs = 100, volume = 0.28f)
            delay(60)
            playCalmChime(frequency = note3, durationMs = 140, volume = 0.30f, chimeDecay = 2.2)
        }
    }

    /**
     * Ambient singing bowl shimmer / crystal wash when hex lines are cleared
     */
    fun playLineClear(linesCount: Int = 1) {
        if (!soundFxEnabled) return
        scope.launch {
            playSingingBowlShimmer(baseFreq = 523.25, durationMs = 280, volume = 0.32f)
            if (linesCount > 1) {
                delay(90)
                playCalmChime(frequency = 659.25, durationMs = 200, volume = 0.26f, chimeDecay = 2.0)
            }
        }
    }

    /**
     * Uplifting celestial harp / celesta arpeggio on win or level up
     */
    fun playWin() {
        if (!soundFxEnabled) return
        scope.launch {
            val notes = listOf(392.00, 523.25, 659.25, 783.99, 1046.50)
            for (freq in notes) {
                playCalmChime(frequency = freq, durationMs = 130, volume = 0.26f, chimeDecay = 2.8)
                delay(95)
            }
        }
    }

    /**
     * Enchanting sparkling crystal chime when a block fills inside the relic shape
     */
    fun playBlockFill(blockIndex: Int = 0) {
        if (!soundFxEnabled) return
        scope.launch {
            val chimeNotes = listOf(392.00, 440.00, 523.25, 587.33, 659.25, 783.99, 880.00)
            val note = chimeNotes[blockIndex % chimeNotes.size]
            playCalmChime(frequency = note, durationMs = 120, volume = 0.30f, chimeDecay = 3.0)
            delay(50)
            playCalmChime(frequency = note * 1.5, durationMs = 160, volume = 0.26f, chimeDecay = 2.2)
        }
    }

    /**
     * Grand celestial resonance when an entire shape wonder is fully restored
     */
    fun playShapeComplete() {
        if (!soundFxEnabled) return
        scope.launch {
            playSingingBowlShimmer(baseFreq = 440.00, durationMs = 400, volume = 0.32f)
            val notes = listOf(440.00, 523.25, 659.25, 880.00, 1046.50)
            for (freq in notes) {
                playCalmChime(frequency = freq, durationMs = 150, volume = 0.28f, chimeDecay = 2.6)
                delay(80)
            }
        }
    }

    /**
     * Comforting, gentle descending lullaby bell on game over
     */
    fun playGameOver() {
        if (!soundFxEnabled) return
        scope.launch {
            val notes = listOf(440.00, 392.00, 349.23, 329.63)
            for (freq in notes) {
                playCalmChime(frequency = freq, durationMs = 140, volume = 0.22f, chimeDecay = 3.0)
                delay(120)
            }
        }
    }

    /**
     * Delicate silver wind chime glockenspiel ping
     */
    fun playCoin() {
        if (!soundFxEnabled) return
        scope.launch {
            playCalmChime(frequency = 783.99, durationMs = 50, volume = 0.22f)
            delay(40)
            playCalmChime(frequency = 1046.50, durationMs = 80, volume = 0.25f, chimeDecay = 3.5)
        }
    }

    /**
     * Peaceful wooden chime progression for shop purchases
     */
    fun playBuy() {
        if (!soundFxEnabled) return
        scope.launch {
            val notes = listOf(392.00, 523.25, 659.25, 783.99)
            for (freq in notes) {
                playCalmChime(frequency = freq, durationMs = 70, volume = 0.24f)
                delay(55)
            }
        }
    }

    /**
     * Gentle deep singing bowl resonance for hammer booster
     */
    fun playHammer() {
        if (!soundFxEnabled) return
        scope.launch {
            playWarmWoodTap(frequency = 180.0, durationMs = 120, volume = 0.32f)
            delay(40)
            playSingingBowlShimmer(baseFreq = 329.63, durationMs = 240, volume = 0.25f)
        }
    }

    /**
     * Soft bamboo breeze flutter for shuffle
     */
    fun playShuffle() {
        if (!soundFxEnabled) return
        scope.launch {
            val notes = listOf(329.63, 392.00, 440.00, 523.25)
            for (freq in notes) {
                playCalmChime(frequency = freq, durationMs = 45, volume = 0.20f)
                delay(35)
            }
        }
    }

    /**
     * Very subtle, soft muted tap on invalid action
     */
    fun playError() {
        if (!soundFxEnabled) return
        scope.launch {
            playWarmWoodTap(frequency = 180.0, durationMs = 60, volume = 0.16f)
        }
    }

    /**
     * Calming ambient pentatonic soundscape loop.
     * Uses non-blocking coroutines and responds instantly to app pause or stop.
     */
    fun startMusic() {
        if (!musicEnabled || isPaused || musicJob?.isActive == true) return
        musicJob = scope.launch {
            val pentatonic = listOf(261.63, 293.66, 329.63, 392.00, 440.00, 523.25)
            val melodyPattern = listOf(0, 2, 4, 3, 2, 0, 3, 5, 4, 2, 1, 0)
            var index = 0
            while (isActive && musicEnabled && !isPaused) {
                val note = pentatonic[melodyPattern[index % melodyPattern.size]]
                playSoftWarmMusicTone(note, durationMs = 380, volume = 0.09f)
                index++
                delay(520)
            }
        }
    }

    fun stopMusic() {
        musicJob?.cancel()
        musicJob = null
        try {
            activeMusicTrack?.pause()
            activeMusicTrack?.flush()
            activeMusicTrack?.release()
            activeMusicTrack = null
        } catch (_: Exception) {}
    }

    /**
     * Soft background ambient piano tone using cooperative coroutine delays
     */
    private suspend fun playSoftWarmMusicTone(frequency: Double, durationMs: Int, volume: Float) {
        if (isPaused) return
        try {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = when {
                    i < numSamples * 0.15 -> i / (numSamples * 0.15)
                    else -> (numSamples - i).toDouble() / (numSamples * 0.85)
                }
                val s1 = sin(2.0 * PI * frequency * t)
                val s2 = 0.25 * sin(4.0 * PI * frequency * t)
                val sample = ((s1 + s2) * 0.7 * envelope * volume * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            outputMusicBuffer(buffer, durationMs)
        } catch (_: Exception) {}
    }

    private suspend fun outputMusicBuffer(buffer: ShortArray, durationMs: Int) {
        if (isPaused) return
        var track: AudioTrack? = null
        try {
            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            activeMusicTrack = track
            track.write(buffer, 0, buffer.size)
            if (!isPaused) {
                track.play()
                delay(durationMs.toLong() + 10)
            }
        } catch (_: Exception) {
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Exception) {}
            if (activeMusicTrack == track) {
                activeMusicTrack = null
            }
        }
    }

    /**
     * Synthesizes a soothing bell / chime with natural exponential decay
     */
    private fun playCalmChime(
        frequency: Double,
        durationMs: Int,
        volume: Float,
        chimeDecay: Double = 3.5
    ) {
        try {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / numSamples

                val attack = if (progress < 0.05) progress / 0.05 else 1.0
                val decay = exp(-chimeDecay * progress)
                val envelope = attack * decay

                val s1 = sin(2.0 * PI * frequency * t)
                val s2 = 0.22 * sin(4.0 * PI * frequency * t)
                val s3 = 0.08 * sin(6.0 * PI * frequency * t)
                val sample = ((s1 + s2 + s3) * 0.75 * envelope * volume * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            outputSfxBuffer(buffer, durationMs)
        } catch (_: Exception) {}
    }

    /**
     * Synthesizes a warm wooden marimba tap with low fundamental and quick gentle decay
     */
    private fun playWarmWoodTap(
        frequency: Double,
        durationMs: Int,
        volume: Float
    ) {
        try {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / numSamples

                val attack = if (progress < 0.08) progress / 0.08 else 1.0
                val decay = exp(-5.0 * progress)
                val envelope = attack * decay

                val s1 = sin(2.0 * PI * frequency * t)
                val s2 = 0.3 * sin(2.0 * PI * (frequency * 1.5) * t)
                val sample = ((s1 + s2) * 0.7 * envelope * volume * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            outputSfxBuffer(buffer, durationMs)
        } catch (_: Exception) {}
    }

    /**
     * Synthesizes an ambient singing bowl shimmer / crystal breeze
     */
    private fun playSingingBowlShimmer(
        baseFreq: Double,
        durationMs: Int,
        volume: Float
    ) {
        try {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / numSamples

                val envelope = if (progress < 0.25) {
                    progress / 0.25
                } else {
                    exp(-2.5 * (progress - 0.25))
                }

                val f1 = sin(2.0 * PI * baseFreq * t)
                val f2 = 0.35 * sin(2.0 * PI * (baseFreq * 1.5) * t)
                val f3 = 0.20 * sin(2.0 * PI * (baseFreq * 2.0) * t)
                val sample = ((f1 + f2 + f3) * 0.65 * envelope * volume * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            outputSfxBuffer(buffer, durationMs)
        } catch (_: Exception) {}
    }

    private fun outputSfxBuffer(buffer: ShortArray, durationMs: Int) {
        if (isPaused) return
        var track: AudioTrack? = null
        try {
            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 10)
        } catch (_: Exception) {
        } finally {
            try {
                track?.release()
            } catch (_: Exception) {}
        }
    }
}
