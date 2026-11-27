package com.smartlakbay.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartlakbay.app.models.*

private val HeaderBlue = Color(0xFF0A4FD6)
private val TabBlue = Color(0xFF0A4FD6)
private val TabInactive = Color(0xFF64708A)
private val GrayText = Color(0xFF64708A)
private val DeleteRed = Color(0xFFDC2626)
private val SetDateBlue = Color(0xFF0284C7)
private val EditPurple = Color(0xFF7C3AED)
private val RedoGreen = Color(0xFF16A34A)
private val OpenGreen = Color(0xFF16A34A)
private val GreenCheck = Color(0xFF22C55E)

private fun fmtTime(minutes: Int): String {
    val h = (minutes / 60) % 24
    val m = minutes % 60
    return "%02d:%02d".format(h, m)
}

/**
 * Trips tab: Upcoming (editable, customizable) & History (archived, uneditable, with Redo Trip feature).
 */
@Composable
fun TripsScreen(navController: NavController) {
    val context = LocalContext.current
    var trips by remember { mutableStateOf(TripStore.all(context)) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Upcoming, 1 = History

    // Date picker dialog state
    var editingTrip by remember { mutableStateOf<SavedTrip?>(null) }
    var tempDate by remember { mutableStateOf("") }

    // Delete confirmation dialog state
    var tripToDelete by remember { mutableStateOf<SavedTrip?>(null) }

    // Expanded trip details state (track expanded trip IDs)
    var expandedTripIds by remember { mutableStateOf(setOf<String>()) }

    val filtered = trips.filter { if (selectedTab == 0) !it.done else it.done }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
            .padding(16.dp)
    ) {
        // Upcoming / History pills
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFE2E8F0))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf("Upcoming", "History")
            tabs.forEachIndexed { idx, label ->
                val active = selectedTab == idx
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (active) TabBlue else Color.Transparent,
                    modifier = Modifier.clickable { selectedTab = idx }
                ) {
                    Text(
                        label,
                        color = if (active) Color.White else TabInactive,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (selectedTab == 0) "No upcoming trips. Plan one from Explore!" else "No trip history yet.",
                    color = GrayText,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered) { trip ->
                    val isExpanded = expandedTripIds.contains(trip.id)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Top Row: Trip title + Expand arrow + Top-right X delete icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Text(trip.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(6.dp))
                                    IconButton(
                                        onClick = {
                                            expandedTripIds = if (isExpanded) {
                                                expandedTripIds - trip.id
                                            } else {
                                                expandedTripIds + trip.id
                                            }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                            contentDescription = if (isExpanded) "Collapse details" else "Expand details",
                                            tint = HeaderBlue
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { tripToDelete = trip },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Delete trip",
                                        tint = DeleteRed
                                    )
                                }
                            }

                            Spacer(Modifier.height(4.dp))
                            Text("📅 Date: ${trip.date}", color = SetDateBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(trip.meta, color = GrayText, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))

                            Spacer(Modifier.height(10.dp))

                            // Action Buttons depending on Upcoming vs History (Image 19)
                            if (selectedTab == 0) {
                                // Upcoming: Edit Itinerary & Date side-by-side
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            val destName = if (trip.name.contains("Taal")) "Taal Heritage Town" else (trip.destination.ifBlank { "Lipa Heritage District" })
                                            navController.navigate("itinerary/$destName/${trip.budget}/${trip.spotKeys}")
                                        },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("✏️ Edit Itinerary", fontSize = 12.sp, color = EditPurple, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            editingTrip = trip
                                            tempDate = trip.date
                                        },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.weight(0.8f).height(36.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("📅 Date", fontSize = 12.sp, color = SetDateBlue, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                // History: Uneditable, stored in account + "🔄 Redo Trip" feature transferring to Upcoming
                                Button(
                                    onClick = {
                                        // Transfer back to Upcoming (done = false)
                                        val updated = trip.copy(done = false, meta = "Redone • ${trip.spotKeys.split(",").size} Places Included")
                                        TripStore.update(context, updated)
                                        trips = TripStore.all(context)
                                        selectedTab = 0 // Switch to Upcoming tab automatically
                                    },
                                    modifier = Modifier.fillMaxWidth().height(40.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = RedoGreen),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("🔄 Redo Trip & Move to Upcoming", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            // Fully Organized Itinerary Details
                            if (isExpanded) {
                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(color = Color(0xFFE2E8F0))
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    if (selectedTab == 0) "Trip Itinerary Details (Full View):" else "Archived History Itinerary (Read-only):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = HeaderBlue
                                )
                                Spacer(Modifier.height(8.dp))

                                val keys = trip.spotKeys.split(",").filter { it.isNotBlank() }
                                val spotList = keys.mapNotNull { k -> SPOTS.find { it.key == k } }
                                val userLoc = 13.9550 to 121.1600

                                spotList.forEachIndexed { sIndex, spot ->
                                    val dist = calculateDistance(userLoc.first, userLoc.second, spot.lat, spot.lng)
                                    val stopTime = fmtTime(9 * 60 + sIndex * 80)

                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                        Column(
                                            modifier = Modifier.width(16.dp).fillMaxHeight(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(GreenCheck)
                                            )
                                            if (sIndex < spotList.lastIndex) {
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .width(2.dp)
                                                        .background(GreenCheck.copy(alpha = 0.5f))
                                                )
                                            }
                                        }
                                        Spacer(Modifier.width(8.dp))

                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                            shape = RoundedCornerShape(12.dp),
                                            elevation = CardDefaults.cardElevation(1.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        stopTime,
                                                        color = HeaderBlue,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                    Spacer(Modifier.weight(1f))
                                                    Text(
                                                        if (spot.price == 0) "FREE" else "₱${spot.price}",
                                                        color = HeaderBlue,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                }
                                                Spacer(Modifier.height(6.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(spot.icon, fontSize = 20.sp)
                                                    Spacer(Modifier.width(8.dp))
                                                    Text(spot.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                                }
                                                Spacer(Modifier.height(4.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("📍 ${dist} km away • ", color = GrayText, fontSize = 11.sp)
                                                    Text(
                                                        if (spot.isOpen) "OPEN NOW" else "CLOSED",
                                                        color = if (spot.isOpen) OpenGreen else Color(0xFFE5484D),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                if (spot.desc.isNotBlank()) {
                                                    Spacer(Modifier.height(2.dp))
                                                    Text(spot.desc, color = GrayText, fontSize = 11.sp)
                                                }

                                                // Order reorder only allowed in Upcoming tab (Image 19)
                                                if (selectedTab == 0) {
                                                    Spacer(Modifier.height(6.dp))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text("Order reorder:", color = GrayText, fontSize = 11.sp)
                                                        Row {
                                                            IconButton(
                                                                onClick = {
                                                                    if (sIndex > 0) {
                                                                        val mutableKeys = keys.toMutableList()
                                                                        val tmp = mutableKeys[sIndex - 1]
                                                                        mutableKeys[sIndex - 1] = mutableKeys[sIndex]
                                                                        mutableKeys[sIndex] = tmp
                                                                        val newKeysStr = mutableKeys.joinToString(",")
                                                                        val updated = trip.copy(spotKeys = newKeysStr)
                                                                        TripStore.update(context, updated)
                                                                        trips = TripStore.all(context)
                                                                    }
                                                                },
                                                                enabled = sIndex > 0,
                                                                modifier = Modifier.size(28.dp)
                                                            ) {
                                                                Icon(Icons.Filled.KeyboardArrowUp, "Move up", tint = if (sIndex > 0) HeaderBlue else Color.LightGray)
                                                            }
                                                            IconButton(
                                                                onClick = {
                                                                    if (sIndex < keys.lastIndex) {
                                                                        val mutableKeys = keys.toMutableList()
                                                                        val tmp = mutableKeys[sIndex]
                                                                        mutableKeys[sIndex] = mutableKeys[sIndex + 1]
                                                                        mutableKeys[sIndex + 1] = tmp
                                                                        val newKeysStr = mutableKeys.joinToString(",")
                                                                        val updated = trip.copy(spotKeys = newKeysStr)
                                                                        TripStore.update(context, updated)
                                                                        trips = TripStore.all(context)
                                                                    }
                                                                },
                                                                enabled = sIndex < keys.lastIndex,
                                                                modifier = Modifier.size(28.dp)
                                                            ) {
                                                                Icon(Icons.Filled.KeyboardArrowDown, "Move down", tint = if (sIndex < keys.lastIndex) HeaderBlue else Color.LightGray)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (selectedTab == 0) {
                                    Spacer(Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            val destName = if (trip.name.contains("Taal")) "Taal Heritage Town" else (trip.destination.ifBlank { "Lipa Heritage District" })
                                            navController.navigate("itinerary/$destName/${trip.budget}/${trip.spotKeys}")
                                        },
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EditPurple),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("✏️ Edit Itinerary Times & Order", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Set Date Dialog
    editingTrip?.let { t ->
        AlertDialog(
            onDismissRequest = { editingTrip = null },
            title = { Text("Set Trip Date (YYYY-MM-DD)") },
            text = {
                OutlinedTextField(
                    value = tempDate,
                    onValueChange = { tempDate = it },
                    singleLine = true,
                    placeholder = { Text("2026-11-20") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val updated = t.copy(date = tempDate, meta = "$tempDate • Places Included")
                    TripStore.update(context, updated)
                    trips = TripStore.all(context)
                    editingTrip = null
                }) { Text("Save Date") }
            },
            dismissButton = {
                TextButton(onClick = { editingTrip = null }) { Text("Cancel") }
            }
        )
    }

    // Delete Confirmation Dialog
    tripToDelete?.let { t ->
        AlertDialog(
            onDismissRequest = { tripToDelete = null },
            title = { Text("Delete Trip", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${t.name}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        TripStore.delete(context, t.id)
                        trips = TripStore.all(context)
                        tripToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeleteRed)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { tripToDelete = null }) { Text("Cancel") }
            }
        )
    }
}
