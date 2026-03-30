package com.ub.tindercards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ub.tindercards.components.*
import com.ub.tindercards.model.Profile
import com.ub.tindercards.model.SwipeDirection

@Composable
fun TinderSwipeScreen(profiles: List<Profile>) {
    var cards by remember { mutableStateOf(profiles) }
    var lastAction by remember { mutableStateOf<String?>(null) }
    var showMatch by remember { mutableStateOf(false) }
    var matchedProfile by remember { mutableStateOf<Profile?>(null) }

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
