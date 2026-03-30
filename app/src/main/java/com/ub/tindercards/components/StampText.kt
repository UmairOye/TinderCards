package com.ub.tindercards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StampText(text: String, color: Color, alpha: Float, rotation: Float, modifier: Modifier) {
    Box(
        modifier = modifier
            .graphicsLayer {
                this.alpha = alpha
                rotationZ = rotation
                scaleX = 0.8f + alpha * 0.2f
                scaleY = 0.8f + alpha * 0.2f
            }
            .background(Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .graphicsLayer { shadowElevation = 8f }
        )
    }
}
