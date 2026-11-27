package com.smartlakbay.app.ui

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.smartlakbay.app.BuildConfig
import com.smartlakbay.app.models.SPOTS
import com.smartlakbay.app.models.calculateDistance

private val HeaderBlue = Color(0xFF0A4FD6)
private val GrayText = Color(0xFF64708A)
private val DarkText = Color(0xFF0F1B3D)

@Composable
fun RouteScreen(navController: NavController, spotKeys: String, tripLabel: String) {
    val spots = remember(spotKeys) {
        spotKeys.split(",").filter { it.isNotBlank() }
            .mapNotNull { k -> SPOTS.find { it.key == k } }
    }
    val apiKey = BuildConfig.MAPS_API_KEY
    val homeLoc = "13.9550,121.1600"

    var activeStop by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }

    // Consistent chain: Stop 1 starts from Home/Current Location. Stop N starts from Stop N-1.
    val origin = if (activeStop == 0) {
        homeLoc
    } else {
        val prev = spots[activeStop - 1]
        "${prev.lat},${prev.lng}"
    }

    val currentSpot = spots.getOrNull(activeStop) ?: spots.firstOrNull() ?: SPOTS[0]
    val destName = currentSpot.name + " Batangas Philippines"

    // Google Maps Embed directions URL for the active stop
    val embedUrl = "https://maps.google.com/maps?saddr=$origin&daddr=${java.net.URLEncoder.encode(destName, "UTF-8")}&t=m&z=15&output=embed&key=$apiKey"

    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body, html { margin: 0; padding: 0; width: 100%; height: 100%; overflow: hidden; background: #EEF3FA; }
                iframe { width: 100%; height: 100%; border: 0; }
            </style>
        </head>
        <body>
            <iframe src="$embedUrl" allowfullscreen="" loading="lazy"></iframe>
        </body>
        </html>
    """.trimIndent()

    val totalDist = remember(spots) {
        var d = 0.0
        val pts = listOf(13.9550 to 121.1600) + spots.map { it.lat to it.lng }
        for (i in 0 until pts.size - 1) {
            d += calculateDistance(pts[i].first, pts[i].second, pts[i + 1].first, pts[i + 1].second)
        }
        Math.round(d * 10) / 10.0
    }
    val driveMinutes = remember(totalDist) { Math.round((totalDist / 40.0) * 60).toInt() }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFEEF3FA))) {
        // WebView displaying real Google Maps road directions to the active stop in the chain
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    loadDataWithBaseURL("https://maps.google.com", htmlContent, "text/html", "UTF-8", null)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (expanded) 1f else 0.58f),
            update = { wv ->
                wv.loadDataWithBaseURL("https://maps.google.com", htmlContent, "text/html", "UTF-8", null)
            }
        )

        if (!expanded) {
            // Stop chips (Stop 1, Stop 2, Stop 3...)
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                spots.forEachIndexed { i, s ->
                    val active = activeStop == i
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (active) HeaderBlue else Color.White,
                        border = if (!active) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D0E0)) else null,
                        modifier = Modifier.clickable { activeStop = i }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(if (active) "🏁" else "🚩", fontSize = 13.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Stop ${i + 1}: " + (if (s.name.length > 16) s.name.take(16) + "…" else s.name),
                                color = if (active) Color.White else DarkText,
                                fontSize = 11.sp,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Expand bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderBlue)
                    .clickable { expanded = true }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("🔍", fontSize = 13.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    "Expand Map Fullscreen",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Route info card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🚗", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "$driveMinutes min",
                            color = HeaderBlue,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.weight(1f))
                        Surface(shape = RoundedCornerShape(50), color = Color(0xFFDCFCE7)) {
                            Text(
                                "Live Traffic",
                                color = Color(0xFF166534),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text("Directions to Stop ${activeStop + 1}: ${currentSpot.name}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.W600)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        currentSpot.desc,
                        color = GrayText,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Destination + controls card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Active Stop ${activeStop + 1}: " + currentSpot.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Distance from origin: " + if (activeStop == 0) {
                                "${calculateDistance(13.9550, 121.1600, currentSpot.lat, currentSpot.lng)} km"
                            } else {
                                val p = spots[activeStop - 1]
                                "${calculateDistance(p.lat, p.lng, currentSpot.lat, currentSpot.lng)} km from Stop $activeStop"
                            },
                            color = GrayText,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFEAF1FF), modifier = Modifier.clickable { expanded = !expanded }) {
                        Text(
                            if (expanded) "Normal View" else "Expand",
                            color = HeaderBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFE1E1), modifier = Modifier.clickable { navController.popBackStack() }) {
                        Text(
                            "Exit",
                            color = Color(0xFFC1121F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
    }
}
