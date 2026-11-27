package com.smartlakbay.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartlakbay.app.models.*

private val HeaderBlue = Color(0xFF0A4FD6)
private val HeaderBlueDeep = Color(0xFF052C9C)
private val GreenBtn = Color(0xFF21C55E)
private val GreenCheck = Color(0xFF22C55E)
private val GrayText = Color(0xFF64708A)

private val CATEGORIES = listOf("All", "Heritage", "Cafés", "Local Eats", "Stores")
private val CATEGORY_MAP = mapOf(
    "All" to null,
    "Heritage" to "heritage",
    "Cafés" to "cafe",
    "Local Eats" to "food",
    "Stores" to "store"
)

@Composable
fun TripCustomizerScreen(
    navController: NavController,
    destination: String,
    budget: Int
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedKeys by remember {
        mutableStateOf(setOf<String>())
    }

    val destinationSpots = remember(destination) {
        val terms = DEST_SPOT_KEYWORDS[destination] ?: emptyList()
        val matched = SPOTS.filter { spot ->
            val hay = (spot.name + " " + spot.desc + " " + spot.category + " " + spot.icon).lowercase()
            terms.any { it.isNotEmpty() && hay.contains(it.lowercase()) }
        }
        if (matched.isNotEmpty()) matched else SPOTS
    }

    val visibleSpots = remember(searchQuery, selectedCategory) {
        val cat = CATEGORY_MAP[selectedCategory]
        destinationSpots.filter { spot ->
            val matchesCat = cat == null || spot.category == cat
            val matchesSearch = searchQuery.isBlank() ||
                (spot.name + " " + spot.desc).lowercase().contains(searchQuery.lowercase())
            matchesCat && matchesSearch
        }
    }

    val currentCost = visibleSpots
        .filter { selectedKeys.contains(it.key) }
        .sumOf { it.price }
    val includedCount = visibleSpots.count { selectedKeys.contains(it.key) }
    val isOverBudget = currentCost > budget

    val userLoc = 13.9550 to 121.1600

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
                Text("Trip Customizer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(50), color = Color(0xFFEAF1FF)) {
                    Text(
                        "Step 2 of 3",
                        color = HeaderBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = GreenBtn,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🤖", fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Lakbay AI", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)) {

            // Budget banner
            item {
                val blueBrush = Brush.horizontalGradient(listOf(HeaderBlue, HeaderBlueDeep))
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(blueBrush)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(destination, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                            Text(
                                "Total budget (per person)",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "₱${java.text.NumberFormat.getNumberInstance(java.util.Locale("en", "PH")).format(budget)}",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Search specific place
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search specific place or attraction...", color = GrayText) },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Filter by category
            item {
                Text("Filter by category", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.W600, color = GrayText, modifier = Modifier.padding(top = 12.dp))
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CATEGORIES.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) HeaderBlue else Color(0xFFF1F5F9),
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                cat,
                                color = if (isSelected) Color.White else Color(0xFF1E293B),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Choose places to include
            item {
                Text("Choose places to include", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.W600, color = GrayText, modifier = Modifier.padding(top = 16.dp))
                Spacer(Modifier.height(8.dp))
            }

            // Scrollable container showing ~3-4 places at a time
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(visibleSpots) { spot ->
                            val isSelected = selectedKeys.contains(spot.key)
                            val dist = calculateDistance(userLoc.first, userLoc.second, spot.lat, spot.lng)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GreenCheck else Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedKeys = if (isSelected) selectedKeys - spot.key else selectedKeys + spot.key
                                    }
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) GreenCheck else Color(0xFFF1F5F9))
                                            .border(
                                                width = if (!isSelected) 1.dp else 0.dp,
                                                color = if (!isSelected) Color(0xFFC7D0E0) else Color.Transparent,
                                                shape = RoundedCornerShape(6.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Text(spot.icon, fontSize = 20.sp)
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(spot.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(
                                            "${spot.category} • ★${spot.rating} • ${dist} km",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = GrayText,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Text(
                                        if (spot.price == 0) "FREE" else "₱${spot.price}",
                                        color = HeaderBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Included spots summary + progress
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        "Included spots: $includedCount of ${visibleSpots.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "₱$currentCost / ₱$budget",
                        color = if (isOverBudget) Color(0xFFE5484D) else HeaderBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(6.dp))
                val fraction = if (budget > 0) minOf(1f, currentCost.toFloat() / budget) else 0f
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50)),
                    color = if (isOverBudget) Color(0xFFE5484D) else GreenCheck,
                    trackColor = Color(0xFFE2E8F0)
                )
                if (isOverBudget) {
                    Text("Over budget", color = Color(0xFFE5484D), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
                Text(
                    "* Note: All prices are estimated costs and may vary on-site.",
                    color = GrayText,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(Modifier.height(16.dp))

                // Generate Itinerary button
                Button(
                    onClick = {
                        val keys = visibleSpots
                            .filter { selectedKeys.contains(it.key) }
                            .map { it.key }
                            .joinToString(",")
                        navController.navigate("itinerary/$destination/$budget/$keys")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenBtn),
                    shape = RoundedCornerShape(14.dp),
                    enabled = includedCount > 0
                ) {
                    Text(
                        "✨ Generate Itinerary ($includedCount)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
