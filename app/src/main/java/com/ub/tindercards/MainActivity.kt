package com.ub.tindercards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ub.tindercards.model.Profile
import com.ub.tindercards.ui.theme.TinderCardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TinderCardsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        TinderSwipeScreen(profiles = getDummyProfiles())
                    }
                }
            }
        }
    }
}

fun getDummyProfiles() = listOf(
    Profile(
        id = 1,
        name = "Alex",
        age = 24,
        distance = "2 miles away",
        job = "Software Engineer",
        bio = "Coffee lover and tech enthusiast",
        imageRes = R.drawable.first,
        tags = listOf("Coding", "Coffee", "Music")
    ),
    Profile(
        id = 2,
        name = "Sam",
        age = 22,
        distance = "5 miles away",
        job = "Designer",
        bio = "Making things look pretty",
        imageRes = R.drawable.fifth,
        tags = listOf("Art", "Design", "Travel")
    ),
    Profile(
        id = 3,
        name = "Jordan",
        age = 26,
        distance = "1 mile away",
        job = "Photographer",
        bio = "Capturing moments that last forever",
        imageRes = R.drawable.second,
        tags = listOf("Photography", "Nature", "Hiking")
    ),
    Profile(
        id = 4,
        name = "Jordan",
        age = 26,
        distance = "1 mile away",
        job = "Photographer",
        bio = "Capturing moments that last forever",
        imageRes = R.drawable.third,
        tags = listOf("Photography", "Nature", "Hiking")
    ),
    Profile(
        id = 5,
        name = "Jordan",
        age = 26,
        distance = "1 mile away",
        job = "Photographer",
        bio = "Capturing moments that last forever",
        imageRes = R.drawable.fourth,
        tags = listOf("Photography", "Nature", "Hiking")
    )
)