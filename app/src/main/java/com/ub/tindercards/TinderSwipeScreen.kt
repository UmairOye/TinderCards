package com.ub.tindercards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

data class Profile(
    val id: Int,
    val name: String,
    val age: Int,
    val distance: String,
    val job: String,
    val bio: String,
    val imageRes: Int,
    val tags: List<String>
)

private const val SWIPE_THRESHOLD = 300f

@Composable
fun TinderSwipeScreen(profiles: List<Profile>) {
    var cards by remember { mutableStateOf(profiles) }
    var lastAction by remember { mutableStateOf<String?>(null) }
    var showMatch by remember { mutableStateOf(false) }
    var matchedProfile by remember { mutableStateOf<Profile?>(null) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0F)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .width(340.dp)
                    .height(480.dp),
                contentAlignment = Alignment.Center
            ) {
                if (cards.isEmpty()) {
                    EmptyState { cards = profiles; lastAction = null }
                } else {
                    cards.take(3).reversed().forEachIndexed { index, profile ->
                        val isTop = index == cards.take(3).size - 1
                        key(profile.id) {
                            SwipeCard(
                                profile = profile,
                                isTop = isTop,
                                behindScale = if (!isTop) 0.95f else 1f,
                                onSwipe = { dir ->
                                    val swiped = cards.first()
                                    lastAction = when (dir) {
                                        SwipeDirection.RIGHT -> "💚 Liked"
                                        SwipeDirection.LEFT -> "❌ Nope"
                                        SwipeDirection.UP -> "⭐ Super Liked"
                                    }
                                    cards = cards.drop(1)
                                    if (dir == SwipeDirection.RIGHT && (0..1).random() == 1) {
                                        matchedProfile = swiped
                                        showMatch = true
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            lastAction?.let {
                Text(
                    text = it,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton3D(
                    icon = Icons.Default.Close,
                    color = Color(0xFFEF4444),
                    size = 64.dp,
                    onClick = {
                        if (cards.isNotEmpty()) {
                            lastAction = "❌ Nope"
                            cards = cards.drop(1)
                        }
                    }
                )
                ActionButton3D(
                    icon = Icons.Default.Star,
                    color = Color(0xFF3B82F6),
                    size = 48.dp,
                    onClick = {
                        if (cards.isNotEmpty()) {
                            lastAction = "⭐ Super Liked"
                            cards = cards.drop(1)
                        }
                    }
                )
                ActionButton3D(
                    icon = Icons.Default.Favorite,
                    color = Color(0xFF22C55E),
                    size = 64.dp,
                    onClick = {
                        if (cards.isNotEmpty()) {
                            val swiped = cards.first()
                            lastAction = "💚 Liked"
                            cards = cards.drop(1)
                            if ((0..1).random() == 1) {
                                matchedProfile = swiped
                                showMatch = true
                            }
                        }
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = showMatch,
            enter = fadeIn(tween(300)) + scaleIn(tween(400)),
            exit = fadeOut(tween(300)) + scaleOut(tween(300))
        ) {
            MatchOverlay(
                profile = matchedProfile,
                onDismiss = { showMatch = false }
            )
        }
    }
}

enum class SwipeDirection { LEFT, RIGHT, UP }

@Composable
private fun SwipeCard(
    profile: Profile,
    isTop: Boolean,
    behindScale: Float,
    onSwipe: (SwipeDirection) -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

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
                    Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    when {
                                        offsetX.value > SWIPE_THRESHOLD -> {
                                            offsetX.animateTo(1000f, tween(300))
                                            onSwipe(SwipeDirection.RIGHT)
                                        }
                                        offsetX.value < -SWIPE_THRESHOLD -> {
                                            offsetX.animateTo(-1000f, tween(300))
                                            onSwipe(SwipeDirection.LEFT)
                                        }
                                        offsetY.value < -SWIPE_THRESHOLD -> {
                                            offsetY.animateTo(-1000f, tween(300))
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
                                    launch { offsetX.snapTo(offsetX.value + dragAmount.x) }
                                    launch { offsetY.snapTo(offsetY.value + dragAmount.y) }
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

@Composable
private fun StampText(text: String, color: Color, alpha: Float, rotation: Float, modifier: Modifier) {
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

@Composable
private fun ActionButton3D(
    icon: ImageVector,
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.75f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 500f),
        label = "btnScale",
        finishedListener = { pressed = false }
    )
    val elevation by animateFloatAsState(
        targetValue = if (pressed) 2f else 8f,
        animationSpec = spring(stiffness = 400f),
        label = "btnElev"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.shadowElevation = elevation
                rotationX = if (pressed) 15f else 0f
                rotationY = if (pressed) -10f else 0f
                cameraDistance = 12f * density
            }
            .shadow(elevation.dp, CircleShape)
            .background(Color(0xFF1A1A2E), CircleShape)
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(Color.Transparent, CircleShape)
                .then(
                    Modifier.graphicsLayer {
                        this.alpha = if (pressed) 0.5f else 0f
                    }
                )
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(size * 0.42f)
        )
    }
}

@Composable
private fun EmptyState(onReset: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E), RoundedCornerShape(24.dp))
    ) {
        Text("🎉", fontSize = 48.sp)
        Spacer(Modifier.height(12.dp))
        Text("No more profiles!", color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text("Come back later", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Start Over")
        }
    }
}

@Composable
private fun MatchOverlay(profile: Profile?, onDismiss: () -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        onDismiss()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎉", fontSize = 56.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "It's a Match!",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFF97316)
            )
            Spacer(Modifier.height(8.dp))
            profile?.let {
                Text(
                    "You and ${it.name} liked each other",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(16.dp))
                Image(
                    painter = painterResource(it.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}
