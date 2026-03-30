package com.ub.tindercards.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ub.tindercards.model.Profile
import com.ub.tindercards.model.SwipeDirection
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

private const val SWIPE_THRESHOLD = 300f

@Composable
fun SwipeCard(
    profile: Profile,
    isTop: Boolean,
    behindScale: Float,
    onSwipe: (SwipeDirection) -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenWidthPx = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val swipeOutDistance = screenWidthPx * 2f

    val rotation by remember { derivedStateOf { offsetX.value / 25f } }
    val likeAlpha by remember { derivedStateOf { (offsetX.value / SWIPE_THRESHOLD).coerceIn(0f, 1f) } }
    val nopeAlpha by remember { derivedStateOf { (-offsetX.value / SWIPE_THRESHOLD).coerceIn(0f, 1f) } }
    val superAlpha by remember { derivedStateOf { (-offsetY.value / SWIPE_THRESHOLD).coerceIn(0f, 1f) } }
    val cardScale by animateFloatAsState(
        targetValue = behindScale,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .graphicsLayer {
                rotationZ = rotation
                scaleX = if (isTop) {
                    val drag = abs(offsetX.value) / 600f
                    (1f - drag * 0.05f).coerceIn(0.95f, 1f)
                } else cardScale
                scaleY = scaleX
                translationY = if (!isTop) 10f else 0f
                alpha = if (!isTop) 0.7f else 1f
                cameraDistance = 12f * density
                rotationX = if (isTop) offsetY.value / 80f else 0f
            }
            .then(
                if (isTop) {
                    Modifier.pointerInput(profile.id) {
                        detectDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    when {
                                        offsetX.value > SWIPE_THRESHOLD -> {
                                            offsetX.animateTo(swipeOutDistance, tween(300))
                                            onSwipe(SwipeDirection.RIGHT)
                                        }
                                        offsetX.value < -SWIPE_THRESHOLD -> {
                                            offsetX.animateTo(-swipeOutDistance, tween(300))
                                            onSwipe(SwipeDirection.LEFT)
                                        }
                                        offsetY.value < -SWIPE_THRESHOLD -> {
                                            offsetY.animateTo(-swipeOutDistance, tween(300))
                                            onSwipe(SwipeDirection.UP)
                                        }
                                        else -> {
                                            launch { offsetX.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = 300f)) }
                                            launch { offsetY.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = 300f)) }
                                        }
                                    }
                                }
                            },
                            onDragCancel = {
                                scope.launch {
                                    launch { offsetX.animateTo(0f, spring()) }
                                    launch { offsetY.animateTo(0f, spring()) }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    offsetX.snapTo(offsetX.value + dragAmount.x)
                                    offsetY.snapTo(offsetY.value + dragAmount.y)
                                }
                            }
                        )
                    }
                } else Modifier
            )
            .clip(RoundedCornerShape(24.dp))
    ) {
        Image(
            painter = painterResource(profile.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.8f)),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        if (likeAlpha > 0f) {
            StampText(
                text = "LIKE",
                color = Color(0xFF4ADE80),
                alpha = likeAlpha,
                rotation = -15f,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 24.dp, top = 32.dp)
            )
        }

        if (nopeAlpha > 0f) {
            StampText(
                text = "NOPE",
                color = Color(0xFFF87171),
                alpha = nopeAlpha,
                rotation = 15f,
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 24.dp, top = 32.dp)
            )
        }

        if (superAlpha > 0f) {
            StampText(
                text = "SUPER",
                color = Color(0xFF60A5FA),
                alpha = superAlpha,
                rotation = 0f,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(profile.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Text("${profile.age}", color = Color.White.copy(alpha = 0.8f), fontSize = 22.sp, fontWeight = FontWeight.Light)
            }
            Spacer(Modifier.height(4.dp))
            Text(profile.job, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            Spacer(Modifier.height(2.dp))
            Text(profile.distance, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            Text(profile.bio, color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                profile.tags.forEach { tag ->
                    Text(
                        text = tag,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
