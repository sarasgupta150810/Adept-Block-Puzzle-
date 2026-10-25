package com.adept.blockpuzzle.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.adept.blockpuzzle.model.FloatingText
import com.adept.blockpuzzle.model.Particle
import kotlin.random.Random

class ParticleSystem {
    private val particles = mutableListOf<Particle>()
    private val floatingTexts = mutableListOf<FloatingText>()
    private var nextId = 0L

    fun getParticles(): List<Particle> = particles
    fun getFloatingTexts(): List<FloatingText> = floatingTexts

    fun spawnBurst(center: Offset, color: Color, count: Int = 20) {
        val rand = Random(System.nanoTime())
        for (i in 0 until count) {
            val angle = rand.nextFloat() * 2f * Math.PI.toFloat()
            val speed = rand.nextFloat() * 300f + 100f
            val vx = kotlin.math.cos(angle) * speed
            val vy = kotlin.math.sin(angle) * speed
            val size = rand.nextFloat() * 9f + 5f

            particles.add(
                Particle(
                    id = nextId++,
                    x = center.x,
                    y = center.y,
                    vx = vx,
                    vy = vy,
                    color = color,
                    size = size,
                    life = 1f,
                    isStar = i % 2 == 0
                )
            )
        }
    }

    /**
     * Upward fountain of magic sparks and shining stars (matching center of Panel 2).
     */
    fun spawnMagicFountain(center: Offset, count: Int = 15) {
        val rand = Random(System.nanoTime())
        val palette = listOf(
            Color(0xFFFFD600), // Gold
            Color(0xFF38BDF8), // Cyan
            Color(0xFFE879F9), // Magenta
            Color(0xFF86EFAC), // Lime
            Color.White
        )

        for (i in 0 until count) {
            val angle = (rand.nextFloat() * 0.6f + 0.2f) * Math.PI.toFloat() // upward cone
            val speed = rand.nextFloat() * 240f + 100f
            val vx = (rand.nextFloat() - 0.5f) * 160f
            val vy = -kotlin.math.sin(angle) * speed
            val size = rand.nextFloat() * 7f + 4f

            particles.add(
                Particle(
                    id = nextId++,
                    x = center.x + (rand.nextFloat() - 0.5f) * 40f,
                    y = center.y + (rand.nextFloat() - 0.5f) * 30f,
                    vx = vx,
                    vy = vy,
                    color = palette.random(),
                    size = size,
                    life = 1f,
                    isStar = rand.nextBoolean()
                )
            )
        }
    }

    fun spawnCoinSparkles(center: Offset, count: Int = 18) {
        val rand = Random(System.nanoTime())
        val goldPalette = listOf(
            Color(0xFFFFD700), // Pure Gold
            Color(0xFFFBBF24), // Amber
            Color(0xFFF59E0B), // Deep Gold
            Color(0xFFFFFBEB), // White shimmer
            Color(0xFFFFE082)  // Soft yellow
        )
        for (i in 0 until count) {
            val angle = rand.nextFloat() * 2f * Math.PI.toFloat()
            val speed = rand.nextFloat() * 220f + 80f
            val vx = kotlin.math.cos(angle) * speed
            val vy = kotlin.math.sin(angle) * speed - 60f // slight upward bias
            particles.add(
                Particle(
                    id = nextId++,
                    x = center.x,
                    y = center.y,
                    vx = vx,
                    vy = vy,
                    color = goldPalette.random(),
                    size = rand.nextFloat() * 8f + 5f,
                    life = 1f,
                    isStar = i % 2 == 0
                )
            )
        }
    }

    fun spawnSmash(center: Offset, count: Int = 26) {
        val rand = Random(System.nanoTime())
        val smashPalette = listOf(
            Color(0xFFEF4444), // Crimson
            Color(0xFFF97316), // Orange
            Color(0xFFFBBF24), // Amber
            Color(0xFFE2E8F0), // Shards
            Color.White
        )
        for (i in 0 until count) {
            val angle = rand.nextFloat() * 2f * Math.PI.toFloat()
            val speed = rand.nextFloat() * 380f + 120f
            val vx = kotlin.math.cos(angle) * speed
            val vy = kotlin.math.sin(angle) * speed
            particles.add(
                Particle(
                    id = nextId++,
                    x = center.x,
                    y = center.y,
                    vx = vx,
                    vy = vy,
                    color = smashPalette.random(),
                    size = rand.nextFloat() * 10f + 4f,
                    life = 1f,
                    isStar = rand.nextBoolean()
                )
            )
        }
    }

    fun spawnFloatingText(text: String, position: Offset, color: Color, sizeSp: Float = 22f) {
        floatingTexts.add(
            FloatingText(
                id = nextId++,
                text = text,
                position = position,
                color = color,
                sizeSp = sizeSp,
                progress = 0f
            )
        )
    }

    fun spawnConfetti(width: Float, height: Float, count: Int = 60) {
        val rand = Random(System.nanoTime())
        val palette = listOf(
            Color(0xFFFF1744), Color(0xFFFFD600), Color(0xFF00E5FF),
            Color(0xFF76FF03), Color(0xFFE040FB), Color(0xFFFF9100)
        )
        for (i in 0 until count) {
            val x = rand.nextFloat() * width
            val y = rand.nextFloat() * (height * 0.4f)
            val speed = rand.nextFloat() * 220f + 80f
            val angle = (rand.nextFloat() * 0.8f + 0.1f) * Math.PI.toFloat() // downward spray
            val vx = kotlin.math.cos(angle) * speed * (if (rand.nextBoolean()) 1 else -1)
            val vy = kotlin.math.sin(angle) * speed

            particles.add(
                Particle(
                    id = nextId++,
                    x = x,
                    y = y,
                    vx = vx,
                    vy = vy,
                    color = palette.random(),
                    size = rand.nextFloat() * 8f + 4f,
                    life = 1f,
                    isStar = rand.nextBoolean()
                )
            )
        }
    }

    fun update(dt: Float) {
        val pIter = particles.iterator()
        while (pIter.hasNext()) {
            val p = pIter.next()
            val newLife = p.life - dt * 1.6f
            if (newLife <= 0f) {
                pIter.remove()
            } else {
                val newX = p.x + p.vx * dt
                val newY = p.y + p.vy * dt + 120f * dt * dt
                val newVx = p.vx * (1f - dt * 0.8f)
                val newVy = p.vy * (1f - dt * 0.8f)
                val index = particles.indexOf(p)
                if (index != -1) {
                    particles[index] = p.copy(
                        x = newX,
                        y = newY,
                        vx = newVx,
                        vy = newVy,
                        life = newLife
                    )
                }
            }
        }

        val tIter = floatingTexts.iterator()
        while (tIter.hasNext()) {
            val t = tIter.next()
            val newProgress = t.progress + dt * 1.4f
            if (newProgress >= 1f) {
                tIter.remove()
            } else {
                val index = floatingTexts.indexOf(t)
                if (index != -1) {
                    floatingTexts[index] = t.copy(
                        progress = newProgress,
                        position = Offset(t.position.x, t.position.y - 70f * dt)
                    )
                }
            }
        }
    }

    fun clear() {
        particles.clear()
        floatingTexts.clear()
    }
}
