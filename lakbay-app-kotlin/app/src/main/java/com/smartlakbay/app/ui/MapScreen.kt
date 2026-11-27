package com.smartlakbay.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.smartlakbay.app.models.SPOTS
import com.smartlakbay.app.models.Spot

private val HeaderBlue = Color(0xFF0A4FD6)
private val GreenBtn = Color(0xFF21C55E)
private val OpenGreen = Color(0xFF16A34A)
private val GrayText = Color(0xFF64708A)

@Composable
fun MapScreen(userLocation: LatLng, navController: NavController? = null) {
    val context = LocalContext.current
    var currentLatLng by remember { mutableStateOf(userLocation) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpot by remember { mutableStateOf<Spot?>(SPOTS.firstOrNull()) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasPermission = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                       perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Real-time GPS location updates
    DisposableEffect(hasPermission) {
        if (!hasPermission) return@DisposableEffect onDispose {}

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateIntervalMillis(1500L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    currentLatLng = LatLng(loc.latitude, loc.longitude)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        } catch (e: SecurityException) {}

        onDispose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLatLng, 14f)
    }

    val filteredSpots = remember(searchQuery) {
        if (searchQuery.isBlank()) SPOTS
        else SPOTS.filter { it.name.contains(searchQuery, true) || it.desc.contains(searchQuery, true) || it.category.contains(searchQuery, true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
    ) {
        // Top Bar matching Image 20
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (navController != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = HeaderBlue)
                        }
                    }
                    Text("🌋 Smart ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = HeaderBlue)
                    Text("Lakbay Map", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GreenBtn)
                    Spacer(Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = HeaderBlue
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.MyLocation, null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("My Location", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // GPS Status Banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(GreenBtn)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Using Live GPS • ${"%.4f".format(currentLatLng.latitude)}, ${"%.4f".format(currentLatLng.longitude)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HeaderBlue
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Search Bar matching Image 20
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search nearby Batangas stores & establishments...", fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Search, "Search") },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }
        }

        // Map & Nearby Establishments split layout matching Image 20
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("🗺️ Batangas Google Map", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F1B3D))
            }

            // Map View Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(isMyLocationEnabled = hasPermission),
                            uiSettings = MapUiSettings(myLocationButtonEnabled = true, zoomControlsEnabled = true)
                        ) {
                            filteredSpots.forEach { spot ->
                                Marker(
                                    state = rememberMarkerState(position = LatLng(spot.lat, spot.lng)),
                                    title = "${spot.icon} ${spot.name}",
                                    snippet = "${spot.category} • ₱${spot.price} • ★${spot.rating}",
                                    onClick = {
                                        selectedSpot = spot
                                        false
                                    }
                                )
                            }
                            Marker(
                                state = rememberMarkerState(position = currentLatLng),
                                title = "📍 You are here",
                                snippet = "Live GPS Location"
                            )
                        }

                        // Selected spot overlay info card on map if any
                        selectedSpot?.let { spot ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                shadowElevation = 6.dp,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp)
                                    .width(240.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(spot.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                    Text("★ ${spot.rating} • ${if (spot.price == 0) "FREE" else "₱${spot.price}"}", fontSize = 11.sp, color = GrayText)
                                    Spacer(Modifier.height(4.dp))
                                    Button(
                                        onClick = {
                                            if (navController != null) {
                                                val enc = java.net.URLEncoder.encode(spot.name, "UTF-8")
                                                navController.navigate("route/${spot.key}/$enc")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = HeaderBlue),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Filled.Directions, null, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Get Directions", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
                Text("Nearby Establishments", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F1B3D))
            }

            // Nearby Establishments List matching Image 20
            items(filteredSpots) { spot ->
                val dist = com.smartlakbay.app.models.calculateDistance(
                    currentLatLng.latitude, currentLatLng.longitude, spot.lat, spot.lng
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedSpot = spot
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(spot.lat, spot.lng), 16f)
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(spot.icon, fontSize = 24.sp)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(spot.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F1B3D))
                                    Text("★ ${spot.rating} • ${dist} km away • ${if (spot.price == 0) "Est. FREE" else "Est. ₱${spot.price}"}", fontSize = 12.sp, color = GrayText)
                                }
                            }
                            // Get Directions Button matching Image 20
                            Button(
                                onClick = {
                                    if (navController != null) {
                                        val enc = java.net.URLEncoder.encode(spot.name, "UTF-8")
                                        navController.navigate("route/${spot.key}/$enc")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenBtn),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Filled.Directions, null, modifier = Modifier.size(14.dp), tint = Color.White)
                                Spacer(Modifier.width(4.dp))
                                Text("Get Directions", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(spot.desc, fontSize = 12.sp, color = GrayText)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFDCFCE7))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    if (spot.isOpen) "OPEN NOW" else "CLOSED",
                                    color = if (spot.isOpen) OpenGreen else Color(0xFFE5484D),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
