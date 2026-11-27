package com.smartlakbay.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Non-dismissible overlay gate shown on top of the dashboard for unauthenticated users.
 */
@Composable
fun AuthGateOverlay(onUnlocked: (String) -> Unit, onGuest: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 340.dp)
                .padding(24.dp),
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                LoginCard(
                    onSuccess = { email -> onUnlocked(email) },
                    onGuest = onGuest,
                    compact = true
                )
            }
        }
    }
}
