package com.adept.blockpuzzle.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.adept.blockpuzzle.R
import com.adept.blockpuzzle.model.GameTheme

/**
 * High-definition radiant sky-blue bokeh wallpaper matching the reference casual puzzle game.
 */
@Composable
fun SkyHexBokehBackground(
    modifier: Modifier = Modifier,
    theme: GameTheme? = null
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_sky_bokeh),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        if (theme != null && theme.id != "cyber_neon") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                theme.bgTop.copy(alpha = 0.65f),
                                theme.bgMid.copy(alpha = 0.55f),
                                theme.bgBottom.copy(alpha = 0.65f)
                            )
                        )
                    )
            )
        }
    }
}
