package com.smartlakbay.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartlakbay.app.models.*

private val HeaderBlue = Color(0xFF0A4FD6)
private val HeaderBlueDeep = Color(0xFF052C9C)
private val GreenBtn = Color(0xFF21C55E)
private val GreenCheck = Color(0xFF22C55E)
private val OpenGreen = Color(0xFF16A34A)
private val GrayText = Color(0xFF64708A)

data class Stop(val spot: Spot, var time: Int)

private fun fmtTime(minutes: Int): String {
    val h = (minutes / 60) % 24
    val m = minutes % 60
    return "%02d:%02d".format(h, m)
}

@Composable
fun ItineraryScreen(
    navController: NavController,
    destination: String,
    budget: Int,
    spotKeys: String
) {
    val context = LocalContext.current
    var stops by remember {
        mutableStateOf(
            spotKeys
                .split(",")
                .filter { it.isNotBlank() }
                .mapNotNull { key -> SPOTS.find { it.key == key } }
                .mapIndexed { index, spot -> Stop(spot, 9 * 60 + index * 80) }
        )
    }
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    val userLoc = 13.9550 to 121.1600
    val totalCost = stops.sumOf { it.spot.price }
    val tripName = "$destination Trip"

    fun moveUp(i: Int) {
        if (i == 0) return
        val newStops = stops.toMutableList()
        val tmp = newStops[i - 1]
        newStops[i - 1] = newStops[i]
        newStops[i] = tmp
        stops = newStops
    }

    fun moveDown(i: Int) {
        if (i >= stops.lastIndex) return
        val newStops = stops.toMutableList()
        val tmp = newStops[i]
        newStops[i] = newStops[i + 1]
        newStops[i + 1] = tmp
        stops = newStops
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = HeaderBlue)
                }
                Text("Your Itinerary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .background(Color(0xFFEAF0FB)),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Trip summary card
            item {
                val blueBrush = Brush.horizontalGradient(listOf(HeaderBlue, HeaderBlueDeep))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                tripName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Total (Estimated Cost)", color = GrayText, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            Text(
                                "₱$totalCost / ₱$budget",
                                color = HeaderBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Text(
                            "* Note: All prices are estimated costs and may vary on-site.",
                            color = GrayText,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Itinerary stops
            items(stops.size) { index ->
                val stop = stops[index]
                val spot = stop.spot
                val dist = calculateDistance(userLoc.first, userLoc.second, spot.lat, spot.lng)

                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    Column(
                        modifier = Modifier.width(16.dp).fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(GreenCheck)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .width(2.dp)
                                .background(GreenCheck.copy(alpha = 0.5f))
                        )
                    }
                    Spacer(Modifier.width(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    fmtTime(stop.time),
                                    color = HeaderBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier
                                        .clickable { editingIndex = index }
                                        .clip(RoundedCornerShape(6.dp))
                                        .padding(vertical = 2.dp, horizontal = 4.dp)
                                )
                                Text("  (tap to change)", color = GrayText, fontSize = 10.sp)
                                Spacer(Modifier.weight(1f))
                                Text(
                                    if (spot.price == 0) "FREE" else "₱${spot.price}",
                                    color = HeaderBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(spot.icon, fontSize = 22.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(spot.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            }
                            Spacer(Modifier.height(4.dp))
                            Row {
                                Text("📍", fontSize = 12.sp)
                                Spacer(Modifier.width(4.dp))
                                Text("${dist} km away •", color = GrayText, fontSize = 12.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    if (spot.isOpen) "OPEN NOW" else "CLOSED",
                                    color = if (spot.isOpen) OpenGreen else Color(0xFFE5484D),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (spot.desc.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(spot.desc, color = GrayText, fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Order:", color = GrayText, fontSize = 11.sp)
                                Spacer(Modifier.width(8.dp))
                                IconButton(onClick = { moveUp(index) }, enabled = index > 0) {
                                    Icon(
                                        Icons.Filled.KeyboardArrowUp,
                                        "Move up",
                                        tint = if (index > 0) HeaderBlue else Color(0xFFC7D0E0),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(onClick = { moveDown(index) }, enabled = index < stops.lastIndex) {
                                    Icon(
                                        Icons.Filled.KeyboardArrowDown,
                                        "Move down",
                                        tint = if (index < stops.lastIndex) HeaderBlue else Color(0xFFC7D0E0),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Save + View route buttons
            item {
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        val today = java.time.LocalDate.now().toString()
                        val newTrip = SavedTrip(
                            id = System.currentTimeMillis().toString(),
                            name = tripName,
                            date = today,
                            meta = "$today • ${stops.size} Places Included",
                            done = false,
                            destination = destination,
                            budget = budget,
                            spotKeys = stops.map { it.spot.key }.joinToString(",")
                        )
                        TripStore.add(context, newTrip)
                        // Immediately direct user to main dashboard
                        navController.navigate("main") {
                            popUpTo("main") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenBtn),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Save trip & View Upcoming Trips", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        val keys = stops.map { it.spot.key }.joinToString(",")
                        val enc = java.net.URLEncoder.encode(tripName, "UTF-8")
                        navController.navigate("route/$keys/$enc")
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderBlue),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("🗺 View route (In Order of Itinerary Trip)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // Time picker dialog
    editingIndex?.let { idx ->
        val initial = stops[idx].time
        var hour by remember { mutableIntStateOf(initial / 60) }
        var minute by remember { mutableIntStateOf(initial % 60) }
        AlertDialog(
            onDismissRequest = { editingIndex = null },
            title = { Text("Change arrival time") },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Hour", color = GrayText, fontSize = 12.sp)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = hour.toString(),
                            onValueChange = { hour = it.toIntOrNull() ?: 0 },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Text(":", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Minute", color = GrayText, fontSize = 12.sp)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = minute.toString(),
                            onValueChange = { minute = it.toIntOrNull() ?: 0 },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val h = (hour.coerceIn(0, 23))
                    val m = (minute.coerceIn(0, 59))
                    val newStops = stops.toMutableList()
                    newStops[idx] = Stop(stops[idx].spot, h * 60 + m)
                    stops = newStops
                    editingIndex = null
                }) { Text("Set time") }
            },
            dismissButton = {
                TextButton(onClick = { editingIndex = null }) { Text("Cancel") }
            }
        )
    }
}
