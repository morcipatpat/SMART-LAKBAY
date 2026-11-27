package com.smartlakbay.app.ui

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.smartlakbay.app.models.AppLanguage
import com.smartlakbay.app.models.UserDatabaseManager

private val BrandBlue = Color(0xFF0A4FD6)
private val BrandGreen = Color(0xFF22C55E)
private val GrayText = Color(0xFF64708A)
private val BorderColor = Color(0xFFE2E8F0)

data class LangOption(val code: String, val name: String, val native: String, val tag: String)

private val ALL_LANGUAGES = listOf(
    LangOption("en", "English", "English", "US"),
    LangOption("fil", "Filipino", "Tagalog", "PH"),
    LangOption("zh", "Chinese", "简体中文", "CN"),
    LangOption("es", "Spanish", "Español", "ES"),
    LangOption("ja", "Japanese", "日本語", "JP"),
    LangOption("ko", "Korean", "한국어", "KR"),
    LangOption("fr", "French", "Français", "FR"),
    LangOption("de", "German", "Deutsch", "DE"),
    LangOption("ar", "Arabic", "العربية", "SA"),
    LangOption("it", "Italian", "Italiano", "IT")
)

private val AVATAR_OPTIONS = listOf("👤", "🌋", "🎒", "🏖️", "🏛️", "🛵", "🌄", "🌟", "✈️", "🗺️")

@Composable
fun ProfileScreen(navController: NavController, onLanguageChanged: () -> Unit = {}) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("smart_lakbay_session", Context.MODE_PRIVATE) }

    val isGuest = prefs.getBoolean("is_guest", false)
    var name by remember { mutableStateOf(prefs.getString("name", "MOJACKO") ?: "MOJACKO") }
    var email by remember { mutableStateOf(prefs.getString("email", "morcillaj14@gmail.com") ?: "morcillaj14@gmail.com") }
    var phone by remember { mutableStateOf(prefs.getString("phone", "+63 912 345 6789") ?: "+63 912 345 6789") }
    var notifsOn by remember { mutableStateOf(prefs.getBoolean("notifications", true)) }
    var selectedLang by remember { mutableStateOf(AppLanguage.getLang(context)) }
    var profileAvatar by remember { mutableStateOf(prefs.getString("profile_avatar", "👤") ?: "👤") }

    var editModalOpen by remember { mutableStateOf(false) }
    var avatarPickerOpen by remember { mutableStateOf(false) }
    var showAllLanguages by remember { mutableStateOf(false) }

    // Device gallery photo picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val uriStr = uri.toString()
            profileAvatar = uriStr
            prefs.edit().putString("profile_avatar", uriStr).apply()
            UserDatabaseManager.syncCurrentSessionToUserFolder(context)
            avatarPickerOpen = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))
        Text(
            AppLanguage.t(context, "profile_title"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F1B3D),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        // Profile Card with Centered Name & Avatar
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isGuest) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 36.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        AppLanguage.t(context, "guest_mode"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F1B3D),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Browsing as Guest",
                        color = GrayText,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        AppLanguage.t(context, "guest_reviews_desc"),
                        color = GrayText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            prefs.edit().clear().commit()
                            navController.navigate("login") {
                                popUpTo("main") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(AppLanguage.t(context, "login_button"), fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    // Logged in User Profile with Device Gallery Photo or Emoji Avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clickable { avatarPickerOpen = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFFEAF1FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (profileAvatar.startsWith("content://") || profileAvatar.startsWith("file://")) {
                                AndroidView(
                                    factory = { ctx ->
                                        ImageView(ctx).apply {
                                            scaleType = ImageView.ScaleType.CENTER_CROP
                                            setImageURI(Uri.parse(profileAvatar))
                                        }
                                    },
                                    update = { iv ->
                                        iv.setImageURI(Uri.parse(profileAvatar))
                                    },
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                Text(profileAvatar, fontSize = 36.sp)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(BrandGreen)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📷", fontSize = 12.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F1B3D),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        email,
                        color = GrayText,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        AppLanguage.t(context, "tap_avatar"),
                        color = BrandGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable { avatarPickerOpen = true }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (!isGuest) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { editModalOpen = true }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Person, null, tint = BrandBlue)
                        Spacer(Modifier.width(16.dp))
                        Text(AppLanguage.t(context, "account_details"), fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ChevronRight, null, tint = GrayText)
                    }

                    HorizontalDivider(color = BorderColor)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Notifications, null, tint = Color(0xFFEAB308))
                        Spacer(Modifier.width(16.dp))
                        Text(AppLanguage.t(context, "notifications"), fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                        Switch(
                            checked = notifsOn,
                            onCheckedChange = {
                                notifsOn = it
                                prefs.edit().putBoolean("notifications", it).apply()
                                UserDatabaseManager.syncCurrentSessionToUserFolder(context)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandGreen)
                        )
                    }

                    HorizontalDivider(color = BorderColor)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Help, null, tint = Color(0xFFEF4444))
                        Spacer(Modifier.width(16.dp))
                        Text(AppLanguage.t(context, "help_support"), fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ChevronRight, null, tint = GrayText)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // App Language Card with Expandable Arrow Button
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Language, null, tint = BrandBlue)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(AppLanguage.t(context, "app_language"), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(AppLanguage.t(context, "select_language_desc"), color = GrayText, fontSize = 12.sp)
                    }
                    Surface(shape = RoundedCornerShape(50), color = Color(0xFFDCFCE7)) {
                        Text(
                            "${selectedLang.uppercase()} EN",
                            color = Color(0xFF166534),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                val displayedLanguages = if (showAllLanguages) ALL_LANGUAGES else ALL_LANGUAGES.take(4)
                val chunks = displayedLanguages.chunked(2)
                chunks.forEach { rowLangs ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowLangs.forEach { lang ->
                            val sel = selectedLang == lang.code
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (sel) Color(0xFFF0FDF4) else Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (sel) BrandGreen else BorderColor),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedLang = lang.code
                                        AppLanguage.setLang(context, lang.code)
                                        UserDatabaseManager.syncCurrentSessionToUserFolder(context)
                                        onLanguageChanged()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(lang.tag, fontSize = 10.sp, color = GrayText, fontWeight = FontWeight.Bold)
                                        Text(lang.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(lang.native, color = GrayText, fontSize = 11.sp)
                                    }
                                    if (sel) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(BrandGreen),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("✓", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAllLanguages = !showAllLanguages }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (showAllLanguages) "Show Less" else AppLanguage.t(context, "view_more_languages"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = if (showAllLanguages) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (showAllLanguages) "Collapse languages" else "Expand languages",
                            tint = BrandBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Sign Out / Login Button
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isGuest) Color(0xFFEFF6FF) else Color(0xFFFFEEEE),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isGuest) Color(0xFFBFDBFE) else Color(0xFFFFCCCC)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Sync before sign out
                    UserDatabaseManager.syncCurrentSessionToUserFolder(context)
                    prefs.edit().clear().commit()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (isGuest) "🔐" else "🚪", fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isGuest) AppLanguage.t(context, "login_button") else "${AppLanguage.t(context, "sign_out")} ($email)",
                    color = if (isGuest) BrandBlue else Color(0xFFDC2626),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    // Avatar & Device Gallery Photo Picker Dialog
    if (avatarPickerOpen) {
        AlertDialog(
            onDismissRequest = { avatarPickerOpen = false },
            title = { Text(AppLanguage.t(context, "select_avatar_title"), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Choose an emoji avatar or upload a photo from your device gallery:", fontSize = 13.sp, color = GrayText)

                    Button(
                        onClick = {
                            galleryLauncher.launch("image/*")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Filled.PhotoLibrary, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Upload Photo from Device Gallery", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    HorizontalDivider(color = BorderColor)

                    Text("Or choose an emoji avatar:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GrayText)

                    val avatarChunks = AVATAR_OPTIONS.chunked(5)
                    avatarChunks.forEach { rowAvatars ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowAvatars.forEach { av ->
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (profileAvatar == av) Color(0xFFEAF1FF) else Color(0xFFF1F5F9),
                                    border = if (profileAvatar == av) androidx.compose.foundation.BorderStroke(2.dp, BrandBlue) else null,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clickable {
                                            profileAvatar = av
                                            prefs.edit().putString("profile_avatar", av).apply()
                                            UserDatabaseManager.syncCurrentSessionToUserFolder(context)
                                            avatarPickerOpen = false
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(av, fontSize = 22.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { avatarPickerOpen = false }) { Text(AppLanguage.t(context, "cancel")) }
            }
        )
    }

    if (editModalOpen) {
        var tempName by remember { mutableStateOf(name) }
        var tempPhone by remember { mutableStateOf(phone) }
        AlertDialog(
            onDismissRequest = { editModalOpen = false },
            title = { Text(AppLanguage.t(context, "account_details")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Name / Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { tempPhone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    name = tempName
                    phone = tempPhone
                    prefs.edit().putString("name", name).putString("phone", phone).apply()
                    UserDatabaseManager.syncCurrentSessionToUserFolder(context)
                    editModalOpen = false
                }) { Text(AppLanguage.t(context, "save")) }
            },
            dismissButton = {
                TextButton(onClick = { editModalOpen = false }) { Text(AppLanguage.t(context, "cancel")) }
            }
        )
    }
}
