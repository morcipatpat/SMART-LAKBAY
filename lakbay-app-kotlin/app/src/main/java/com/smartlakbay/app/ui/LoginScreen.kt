package com.smartlakbay.app.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val BrandBlue = Color(0xFF0A4FD6)
private val BrandGreen = Color(0xFF21C55E)
private val PageBg = Color(0xFFF7F9FC)
private val CardWhite = Color.White
private val GrayText = Color(0xFF64708A)
private val FieldBorder = Color(0xFFC7D0E0)
private val DarkText = Color(0xFF0F1B3D)

private class DemoAccounts(private val context: Context) {
    private val prefs = context.getSharedPreferences("smart_lakbay_accounts", Context.MODE_PRIVATE)

    fun all(): List<Pair<String, String>> =
        prefs.getString("list", null)
            ?.split("\n")
            ?.filter { it.isNotBlank() }
            ?.map { val p = it.split("|"); p[0] to (p.getOrNull(1) ?: "") }
            ?: emptyList()

    fun find(email: String) = all().firstOrNull { it.first.equals(email, true) }

    fun add(email: String, password: String) = prefs.edit()
        .putString("list", (all() + (email to password)).joinToString("\n") { "${it.first}|${it.second}" })
        .apply()
}

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val accounts = remember { DemoAccounts(context) }

    var mode by remember { mutableIntStateOf(0) } // 0 = sign in, 1 = register
    var email by remember { mutableStateOf("morcillaj14@gmail.com") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    fun submit() {
        error = ""
        val e = email.trim()
        if (!e.contains("@") || !e.contains(".")) {
            error = "Please enter a valid email address"
            return
        }
        if (password.length < 6) {
            error = "Password must be at least 6 characters"
            return
        }

        // Save session in smart_lakbay_session and restore user profile from persistent folder database
        val prefs = context.getSharedPreferences("smart_lakbay_session", Context.MODE_PRIVATE)
        val defaultName = e.split("@")[0].replaceFirstChar { it.uppercase() }
        com.smartlakbay.app.models.UserDatabaseManager.restoreUserSessionFromFolder(context, e)
        if (prefs.getString("name", null).isNullOrBlank()) {
            prefs.edit().putString("email", e).putString("name", defaultName).remove("is_guest").commit()
            com.smartlakbay.app.models.UserDatabaseManager.saveUserData(context, e, defaultName, "+63 912 345 6789", "👤", "en", true)
        } else {
            prefs.edit().putString("email", e).remove("is_guest").commit()
        }

        when (mode) {
            0 -> {
                val acc = accounts.find(e)
                if (acc == null) error = "No account found with that email. Switch to Register."
                else if (acc.second != password) error = "Incorrect password. Try again."
                else {
                    prefs.edit().putString("email", e).putString("name", defaultName).remove("is_guest").commit()
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            else -> {
                if (password != confirmPassword) {
                    error = "Passwords do not match. Please retype."
                    return
                }
                if (accounts.find(e) != null) error = "Account already exists. Please sign in."
                else {
                    accounts.add(e, password)
                    prefs.edit().putString("email", e).putString("name", defaultName).remove("is_guest").commit()
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header: volcano badge + app name
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(BrandBlue),
                contentAlignment = Alignment.Center
            ) {
                Text("🌋", fontSize = 20.sp)
            }
            Spacer(Modifier.width(10.dp))
            Text(
                "Smart Lakbay",
                color = BrandBlue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            "Sign in or register your account with Firebase",
            color = GrayText,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 20.dp)
        )

        // Sign In / Register segmented tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, FieldBorder, RoundedCornerShape(10.dp))
                .background(CardWhite)
                .padding(4.dp)
        ) {
            val tabs = listOf("Sign In", "Register")
            tabs.forEachIndexed { index, label ->
                val selected = mode == index
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) Color(0xFFEAF1FF) else Color.Transparent)
                        .clickable { mode = index; error = "" }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        label,
                        color = if (selected) BrandBlue else GrayText,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email address", color = GrayText, fontSize = 14.sp) },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = BrandBlue, fontSize = 15.sp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CardWhite,
                unfocusedContainerColor = CardWhite,
                focusedIndicatorColor = BrandBlue,
                unfocusedIndicatorColor = FieldBorder,
                cursorColor = BrandBlue
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Password field
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = GrayText, fontSize = 14.sp) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = DarkText, fontSize = 15.sp),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CardWhite,
                    unfocusedContainerColor = CardWhite,
                    focusedIndicatorColor = BrandBlue,
                    unfocusedIndicatorColor = FieldBorder,
                    cursorColor = BrandBlue
                ),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, FieldBorder, RoundedCornerShape(10.dp))
                    .background(CardWhite)
                    .clickable { showPassword = !showPassword },
                contentAlignment = Alignment.Center
            ) {
                Text(if (showPassword) "🙈" else "👁️", fontSize = 15.sp)
            }
        }

        // Retype Password field (only shown during registration)
        AnimatedVisibility(visible = mode == 1) {
            Column {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Retype password", color = GrayText, fontSize = 14.sp) },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = DarkText, fontSize = 15.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite,
                        focusedIndicatorColor = BrandBlue,
                        unfocusedIndicatorColor = FieldBorder,
                        cursorColor = BrandBlue
                    ),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Forgot password link (only for sign in)
        if (mode == 0) {
            Text(
                "Forgot password?",
                color = GrayText,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clickable { }
            )
        }

        if (error.isNotEmpty()) {
            Text(error, color = Color(0xFFE5484D), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(14.dp))

        // Green action button
        Button(
            onClick = { submit() },
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
            Text(if (mode == 0) "Sign In" else "Create Account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(Modifier.height(14.dp))

        // Bottom switch link
        Text(
            text = if (mode == 0) "New user? Create account" else "Already have an account? Sign In",
            color = GrayText,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { mode = 1 - mode; error = "" }
        )

        Spacer(Modifier.height(8.dp))

        // Guest access — skips straight to intro or main
        Text(
            "Explore as Guest",
            color = GrayText,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val prefs = context.getSharedPreferences("smart_lakbay_session", Context.MODE_PRIVATE)
                    prefs.edit().putBoolean("is_guest", true).putString("email", "guest@lakbay.app").commit()
                    navController.navigate("intro")
                }
        )
    }
}
