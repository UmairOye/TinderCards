package com.ub.tindercards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmptyState(onReset: () -> Unit) {
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
