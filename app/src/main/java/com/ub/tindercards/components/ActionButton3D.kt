package com.ub.tindercards.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ActionButton3D(
    icon: ImageVector,
    color: Color,
    size: Dp,
    onClick: () -> Unit,
    externalPressed: Boolean = false
) {
    var internalPressed by remember { mutableStateOf(false) }
    val isPressed = internalPressed || externalPressed

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "btnScale"
    )

    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 12f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "btnElev"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.shadowElevation = elevation.dp.toPx()
            }
            .shadow(elevation.dp, CircleShape)
            .background(Color(0xFF1A1A2E).copy(alpha = 0.9f), CircleShape)
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        internalPressed = true
                        tryAwaitRelease()
                        internalPressed = false
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(size * 0.45f)
        )
    }
}
