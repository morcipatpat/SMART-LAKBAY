package com.smartlakbay.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartlakbay.app.models.getDestinationImages

private val BrandBlue = Color(0xFF0A4FD6)
private val BrandGreen = Color(0xFF21C55E)
private val TierChips = listOf("Budget", "Standard", "Luxury")
private val TierPresets = mapOf("Budget" to 1500, "Standard" to 3500, "Luxury" to 8000)

/**
 * Destination detail view, shown as a medium-size container card on the Explore tab
 * with clean layout (no thumbnail row) and tap-to-view swipeable photo gallery modal.
 */
@Composable
fun DestinationDetailCard(destinationName: String, onSearchTrips: (String, Int) -> Unit) {
    val images = getDestinationImages(destinationName)
    val allImages = listOf(images.main) + images.views
    val desc = destinationDesc(destinationName)

    var showGalleryModal by remember { mutableStateOf(false) }

    // Budget planner state
    var budgetText by remember { mutableStateOf("3500") }
    var selectedTier by remember { mutableStateOf("Standard") }
    val currentBudget = budgetText.replace(",", "").toIntOrNull() ?: 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Destination in Batangas", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(Modifier.height(8.dp))

            // Main photo with "Tap to view & swipe" overlay — clicking opens swipeable gallery modal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showGalleryModal = true }
            ) {
                Image(
                    painter = painterResource(images.main),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (allImages.size > 1) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(8.dp)
                    ) {
                        Text(
                            "Tap photo to view & swipe (${allImages.size} photos)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Name + description
            Text(destinationName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(desc, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(Modifier.height(12.dp))

            // ---- Budget planner (per person) ----
            Text("Total budget (per person)", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text(
                "₱${if (currentBudget == 0) "0" else java.text.NumberFormat.getNumberInstance(java.util.Locale("en", "PH")).format(currentBudget)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = BrandBlue
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = budgetText,
                onValueChange = { v ->
                    if (v.all { it.isDigit() } || v.isEmpty()) {
                        budgetText = v
                        selectedTier = ""
                    }
                },
                placeholder = { Text("Enter amount", color = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(Modifier.height(10.dp))

            // Budget tier selector (Budget / Standard / Luxury)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                TierChips.forEach { tier ->
                    val isSelected = selectedTier == tier
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) BrandBlue else Color(0xFFF1F5F9))
                            .border(
                                width = if (isSelected) 0.dp else 1.dp,
                                color = if (isSelected) Color.Transparent else Color(0xFFC7D0E0),
                                shape = RoundedCornerShape(50)
                            )
                            .clickable {
                                selectedTier = tier
                                budgetText = TierPresets[tier].toString()
                            }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            tier,
                            color = if (isSelected) Color.White else Color(0xFF1E293B),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Green Search Batangas Trips button
            Button(
                onClick = { onSearchTrips(destinationName, currentBudget) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Search Batangas Trips", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }

    // Swipeable Photo Gallery Dialog (tapped from main photo)
    if (showGalleryModal) {
        val pagerState = rememberPagerState(pageCount = { allImages.size })
        AlertDialog(
            onDismissRequest = { showGalleryModal = false },
            shape = RoundedCornerShape(16.dp),
            title = { Text(destinationName, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) { page ->
                        Image(
                            painter = painterResource(allImages[page]),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("Photo ${pagerState.currentPage + 1} of ${allImages.size}", style = MaterialTheme.typography.labelSmall)
                }
            },
            confirmButton = {
                TextButton(onClick = { showGalleryModal = false }) { Text("Close") }
            }
        )
    }
}

private fun destinationDesc(name: String): String = when {
    name.contains("Taal") -> "Explore historical Taal Basilica, heritage Spanish colonial houses, and enjoy authentic Batangas kapeng barako."
    name.contains("Café de Lipa") || name.contains("Lipa") -> "Taste legendary Batangas kapeng barako, fresh pastries, and a peaceful heritage district vibe."
    name.contains("Nasugbu") -> "Relax on sandy white beaches, watch stunning sunsets, and unwind at seaside resorts."
    name.contains("Baguio") -> "Cool pine forests, colonial architecture, and iconic landmarks like the pink Baguio Cathedral."
    name.contains("Camp John Hay") -> "A peaceful eco-trail surrounded by pine trees, heritage cottages, and mountain air."
    name.contains("People") || name.contains("Tagaytay") -> "Breathtaking views of Taal Lake and volcano from an elevated ridge park."
    name.contains("Twin Lakes") -> "Scenic cliffside views overlooking Taal from a charming European-style estate."
    name.contains("Vigan") || name.contains("Crisologo") -> "Walk the UNESCO-listed cobbled Calle Crisologo with Spanish colonial houses."
    name.contains("La Union") || name.contains("San Juan") -> "Ride the waves of the surfing capital of Northern Luzon."
    name.contains("Bicol") || name.contains("Cagsawa") -> "Stand at the 16th-century ruins with the majestic Mayon Volcano backdrop."
    name.contains("Manila") || name.contains("Intramuros") -> "Walk the walled city of Manila, a UNESCO heritage site."
    name.contains("Taguig") || name.contains("BGC") -> "A premier lifestyle district with seaside promenades and shops."
    else -> "Discover this destination's sights, local eats, and hidden gems."
}
