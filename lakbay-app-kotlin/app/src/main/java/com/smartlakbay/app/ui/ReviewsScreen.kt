package com.smartlakbay.app.ui

import android.content.Context
import android.net.Uri
import android.widget.ImageView
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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.smartlakbay.app.models.AppLanguage
import com.smartlakbay.app.models.CommunityComment
import com.smartlakbay.app.models.CommunityReview
import com.smartlakbay.app.models.CommunityStore

private val HeaderBlue = Color(0xFF0A4FD6)
private val GreenBtn = Color(0xFF21C55E)
private val GrayText = Color(0xFF64708A)
private val CardBg = Color(0xFFF8FAFC)

@Composable
fun ReviewsScreen(navController: NavController? = null) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("smart_lakbay_session", Context.MODE_PRIVATE) }
    val isGuest = prefs.getBoolean("is_guest", false)
    val userName = prefs.getString("name", if (isGuest) "Guest Traveler" else "John Ray") ?: "John Ray"

    var reviews by remember { mutableStateOf(CommunityStore.all(context)) }

    // New review state (inline post box matching Image 1 & 13)
    var reviewText by remember { mutableStateOf("") }
    var selectedTrip by remember { mutableStateOf("Taal Heritage Trip") }
    var selectedRating by remember { mutableIntStateOf(5) }

    // Real media attachment state (photos & videos from device)
    var attachedMediaUri by remember { mutableStateOf<String?>(null) }
    var attachedMediaType by remember { mutableStateOf<String?>(null) } // "photo" or "video"

    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val uriStr = uri.toString()
            attachedMediaUri = uriStr
            val type = context.contentResolver.getType(uri) ?: ""
            attachedMediaType = if (type.startsWith("video") || uriStr.contains("video", true)) "video" else "photo"
        }
    }

    var commentInputs by remember { mutableStateOf(mapOf<String, String>()) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        "Traveler Reviews & Community",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F1B3D),
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                    Text(
                        "Share your itinerary experiences and connect with fellow travelers",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            // --- Post Itinerary Review Card (Visible to all users including guests) ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✍️", fontSize = 18.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(AppLanguage.t(context, "post_review_title"), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = HeaderBlue)
                        }

                        // Trip selection pills
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(AppLanguage.t(context, "select_trip_label"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GrayText)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Taal Heritage Trip", "Nasugbu Beach Weekend").forEach { trip ->
                                    val isSelected = selectedTrip == trip
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = if (isSelected) HeaderBlue else Color.White,
                                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                                        modifier = Modifier.clickable { selectedTrip = trip }
                                    ) {
                                        Text(
                                            trip,
                                            color = if (isSelected) Color.White else HeaderBlue,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Rating stars selection
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(AppLanguage.t(context, "rating_label"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GrayText)
                            Spacer(Modifier.width(4.dp))
                            for (i in 1..5) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = "$i stars",
                                    tint = if (i <= selectedRating) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { selectedRating = i }
                                )
                            }
                        }

                        // Text box for review
                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            placeholder = { Text(AppLanguage.t(context, "review_placeholder"), color = GrayText, fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        // Attachment & Post Button row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    mediaLauncher.launch("*/*")
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                            ) {
                                Icon(Icons.Filled.AttachFile, null, modifier = Modifier.size(16.dp), tint = HeaderBlue)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    if (attachedMediaUri != null) "📎 Attached (${attachedMediaType ?: "media"})" else AppLanguage.t(context, "attach_media"),
                                    fontSize = 12.sp,
                                    color = HeaderBlue
                                )
                            }

                            Button(
                                onClick = {
                                    if (reviewText.isNotBlank()) {
                                        val newRev = CommunityReview(
                                            id = System.currentTimeMillis().toString(),
                                            author = userName,
                                            tripName = selectedTrip,
                                            text = reviewText,
                                            date = "Nov 14, 2026",
                                            rating = selectedRating,
                                            mediaType = attachedMediaType,
                                            mediaResName = attachedMediaUri,
                                            comments = mutableListOf()
                                        )
                                        CommunityStore.addReview(context, newRev)
                                        reviews = CommunityStore.all(context)
                                        reviewText = ""
                                        attachedMediaUri = null
                                        attachedMediaType = null
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenBtn),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(AppLanguage.t(context, "post_review_btn"), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    AppLanguage.t(context, "recent_reviews"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F1B3D)
                )
            }

            // Reviews feed items (Visible to all users)
            items(reviews) { r ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(r.author, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F1B3D))
                                Text(r.tripName, color = HeaderBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text(r.date, color = GrayText, fontSize = 11.sp)
                        }

                        Spacer(Modifier.height(6.dp))

                        // Star rating display
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            for (i in 1..5) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = if (i <= r.rating) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        Text(r.text, fontSize = 14.sp, color = Color(0xFF1E293B), lineHeight = 20.sp)

                        if (!r.mediaResName.isNullOrBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE2E8F0),
                                modifier = Modifier.fillMaxWidth().height(160.dp)
                            ) {
                                if (r.mediaResName.startsWith("content://") || r.mediaResName.startsWith("file://")) {
                                    if (r.mediaType == "video") {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("🎥 Attached Video\n(${r.mediaResName.takeLast(25)})", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                        }
                                    } else {
                                        AndroidView(
                                            factory = { ctx ->
                                                ImageView(ctx).apply {
                                                    scaleType = ImageView.ScaleType.CENTER_CROP
                                                    setImageURI(Uri.parse(r.mediaResName))
                                                }
                                            },
                                            update = { iv ->
                                                iv.setImageURI(Uri.parse(r.mediaResName))
                                            },
                                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                                        )
                                    }
                                } else {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("📷 Attached Media: ${r.mediaResName}", fontSize = 12.sp, color = GrayText, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        // Comments section
                        if (r.comments.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(Modifier.height(8.dp))
                            Text("${AppLanguage.t(context, "comments")} (${r.comments.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = HeaderBlue)
                            Spacer(Modifier.height(4.dp))
                            r.comments.forEach { c ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(c.author, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F1B3D))
                                        Text(c.text, fontSize = 12.sp, color = Color(0xFF334155))
                                    }
                                }
                            }
                        }

                        // Add comment input
                        Spacer(Modifier.height(10.dp))
                        val currentInput = commentInputs[r.id] ?: ""
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = currentInput,
                                onValueChange = { commentInputs = commentInputs + (r.id to it) },
                                placeholder = { Text(AppLanguage.t(context, "add_comment_placeholder"), color = GrayText, fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(50),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )
                            Spacer(Modifier.width(6.dp))
                            IconButton(onClick = {
                                val text = commentInputs[r.id] ?: ""
                                if (text.isNotBlank()) {
                                    CommunityStore.addComment(context, r.id, CommunityComment(System.currentTimeMillis().toString(), userName, text))
                                    reviews = CommunityStore.all(context)
                                    commentInputs = commentInputs + (r.id to "")
                                }
                            }) {
                                Icon(Icons.Filled.Send, "Send Comment", tint = HeaderBlue, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}
