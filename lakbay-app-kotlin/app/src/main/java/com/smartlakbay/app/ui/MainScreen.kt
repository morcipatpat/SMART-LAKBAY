package com.smartlakbay.app.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartlakbay.app.models.AppLanguage
import com.smartlakbay.app.models.DESTINATIONS

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val icon: String,
    val type: String // "announcement", "reminder", "arrival", "congrats"
)

@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("smart_lakbay_session", Context.MODE_PRIVATE) }

    var authVersion by remember { mutableIntStateOf(0) }
    var langVersion by remember { mutableIntStateOf(0) }
    var showChatBot by remember { mutableStateOf(false) }

    val _av = authVersion
    val savedEmail = prefs.getString("email", null)
    val isGuest = prefs.getBoolean("is_guest", false)
    val isLoggedIn = !savedEmail.isNullOrBlank() || isGuest

    var selectedTab by remember { mutableIntStateOf(0) }

    val tabKeys = listOf("explore", "map", "trips", "reviews", "profile")
    val icons = listOf(Icons.Filled.Explore, Icons.Filled.Map, Icons.Filled.Favorite, Icons.Filled.Star, Icons.Filled.Person)

    var selectedDestinationName by remember { mutableStateOf(DESTINATIONS[0].name) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                val currentLang = langVersion
                NavigationBar {
                    tabKeys.forEachIndexed { index, key ->
                        val label = AppLanguage.t(context, key)
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = {
                                if (key == "map") navController.navigate("map")
                                else selectedTab = index
                            },
                            label = { Text(label, fontSize = 10.sp) },
                            icon = { Icon(icons[index], contentDescription = label) }
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showChatBot = true }, containerColor = Color(0xFF22C55E)) {
                    Icon(Icons.Filled.Chat, "AI Chat", tint = Color.White)
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                val _lv = langVersion
                when (selectedTab) {
                    0 -> ExploreTab(navController, selectedDestinationName, isLoggedIn) { newName -> selectedDestinationName = newName }
                    2 -> TripsTabOrGuest(navController)
                    3 -> ReviewsScreen(navController)
                    4 -> ProfileScreen(navController) {
                        langVersion++
                    }
                }
            }
        }

        // AI Chat Overlay (half-size bottom sheet over semi-transparent backdrop showing dashboard on top half - Image 29)
        if (showChatBot) {
            ChatOverlay(onClose = { showChatBot = false })
        }

        // Only pop up login gate once if unauthenticated
        if (!isLoggedIn) {
            AuthGateOverlay(
                onUnlocked = { email ->
                    prefs.edit().putString("email", email).remove("is_guest").commit()
                    authVersion++
                },
                onGuest = {
                    prefs.edit().putBoolean("is_guest", true).putString("email", "guest@lakbay.app").commit()
                    authVersion++
                }
            )
        }
    }
}

@Composable
private fun TripsTabOrGuest(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("smart_lakbay_session", Context.MODE_PRIVATE) }
    val isGuest = prefs.getBoolean("is_guest", false)

    if (isGuest) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Text("🔒", fontSize = 40.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    AppLanguage.t(context, "guest_trips_title"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    AppLanguage.t(context, "guest_trips_desc"),
                    color = Color(0xFF64708A),
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        prefs.edit().clear().commit()
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(AppLanguage.t(context, "login_button"), fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    } else {
        TripsScreen(navController)
    }
}

@Composable
private fun ExploreTab(
    navController: NavController,
    selectedDestinationName: String,
    isLoggedIn: Boolean,
    onSelectDestination: (String) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("smart_lakbay_session", Context.MODE_PRIVATE) }
    val isGuest = prefs.getBoolean("is_guest", false)

    val profileName = when {
        isGuest -> "Guest"
        isLoggedIn -> prefs.getString("name", "Traveler") ?: "Traveler"
        else -> "Sign In"
    }

    var searchQuery by remember { mutableStateOf("") }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    val notificationsList = remember {
        listOf(
            AppNotification(
                "1",
                "Welcome Back, Traveler! 🌋",
                "Welcome back! Are you ready for another unforgettable Batangas trip today? Discover hidden gems and pristine beaches with AI planning.",
                "Just now",
                "🌟",
                "announcement"
            ),
            AppNotification(
                "2",
                "Upcoming Trip Reminder 📅",
                "Your 'Taal Heritage Trip' is scheduled for Nov 8, 2026. Pack your camera and get ready for an amazing historical walking tour!",
                "2 hours ago",
                "🎒",
                "reminder"
            ),
            AppNotification(
                "3",
                "Destination Arrival Alert 📍",
                "You have arrived at your planned destination! Enjoy exploring the breathtaking sights and local delicacies.",
                "Yesterday",
                "📍",
                "arrival"
            ),
            AppNotification(
                "4",
                "Itinerary Accomplished! 🎉",
                "Congratulations! You successfully completed your Nasugbu Beach Weekend itinerary. Thank you for exploring with Smart Lakbay!",
                "3 days ago",
                "🏆",
                "congrats"
            ),
            AppNotification(
                "5",
                "New App Announcement 🚀",
                "You can now upload photos and videos to your traveler reviews and easily reorder or edit your itinerary times and stops!",
                "5 days ago",
                "📢",
                "announcement"
            )
        )
    }

    val filteredDestinations = remember(searchQuery) {
        if (searchQuery.isBlank()) DESTINATIONS
        else DESTINATIONS.filter { it.name.contains(searchQuery, true) || it.town.contains(searchQuery, true) }
    }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            // Header: Smart Lakbay + Notification Bell + Profile Name / Sign In button
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌋 ", fontSize = 20.sp)
                        Text("Smart ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF0A4FD6))
                        Text("Lakbay", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
                    }
                }
                // Notification Bell Button
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFEAF1FF),
                    modifier = Modifier.clickable { showNotificationsDialog = true }
                ) {
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🔔", fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isLoggedIn) Color(0xFF0A4FD6) else Color(0xFF22C55E),
                    modifier = Modifier.clickable {
                        if (!isLoggedIn) {
                            navController.navigate("login")
                        }
                    }
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isLoggedIn) "👤 " else "🔑 ", fontSize = 12.sp)
                        Text(profileName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            // Wonderful Greeting Box
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF1FF))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Welcome back, $profileName! 🌋", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0A4FD6))
                    Spacer(Modifier.height(4.dp))
                    Text("Are you ready for another unforgettable trip today? Discover Batangas heritage, beaches & mountain trails.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1E293B))
                }
            }
        }
        item {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(AppLanguage.t(context, "search_placeholder")) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Search, "Search") },
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )
        }
        item { Text(AppLanguage.t(context, "popular_destinations"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }

        // Fixed-height scrollable box showing ~3 items at a time
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredDestinations) { d ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectDestination(d.name)
                                },
                            elevation = CardDefaults.cardElevation(if (d.name == selectedDestinationName) 4.dp else 1.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (d.name == selectedDestinationName) Color(0xFFEAF1FF) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(d.icon, fontSize = 26.sp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(d.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(d.dist, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Medium-size container displaying selected destination details
        item {
            Spacer(Modifier.height(4.dp))
            DestinationDetailCard(
                destinationName = selectedDestinationName,
                onSearchTrips = { dest, budget -> navController.navigate("customizer/$dest/$budget") }
            )
        }
    }

    // Notifications & Announcements Dialog (Bell Icon Click)
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔔", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Notifications & Announcements", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.height(340.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(notificationsList) { notif ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(notif.icon, fontSize = 18.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0A4FD6), modifier = Modifier.weight(1f))
                                    Text(notif.time, fontSize = 10.sp, color = Color(0xFF64708A))
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(notif.message, fontSize = 12.sp, color = Color(0xFF334155))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A4FD6)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        )
    }
}
