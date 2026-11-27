package com.smartlakbay.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartlakbay.app.R

private val DeepBlueTop = Color(0xFF0C1F45)
private val DeepBlueBottom = Color(0xFF030B1C)
private val GreenBtn = Color(0xFF22C55E)
private val TextWhite = Color.White
private val TextMuted = Color(0xFF94A3B8)

@Composable
fun IntroScreen(navController: NavController) {
    val gradient = Brush.verticalGradient(listOf(DeepBlueTop, DeepBlueBottom))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(32.dp))

        // Center Content: Much bigger logo, title, tagline & description
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Logo Icon (Suitcase with airplane and orbit) — Much bigger
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_smart_lakbay_logo),
                    contentDescription = "SmartLakbay Logo",
                    modifier = Modifier.size(112.dp)
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                "SmartLakbay",
                color = TextWhite,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center
            )

            Text(
                "Travel planning, made dependable",
                color = TextMuted,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            Text(
                "Your AI travel companion for exploring every corner of Batangas — beaches, heritage & mountain trails.",
                color = TextMuted.copy(alpha = 0.9f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }

        // Bottom Action Button — leads directly to dashboard with popup gate
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            Button(
                onClick = { navController.navigate("main") },
                colors = ButtonDefaults.buttonColors(containerColor = GreenBtn),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "Maglakbay Na! →",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
