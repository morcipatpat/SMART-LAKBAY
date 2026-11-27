package com.smartlakbay.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartlakbay.app.engine.BrainContext
import com.smartlakbay.app.engine.LakbayBrain
import com.smartlakbay.app.models.SPOTS

data class ChatMessage(val role: String, val text: String)

private val Blue = Color(0xFF0A4FD6)
private val DarkText = Color(0xFF1E293B)
private val BoldName = Color(0xFF0F1B3D)
private val SubNote = Color(0xFF475569)

@Composable
fun ChatRichText(text: String, isUser: Boolean) {
    if (isUser) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.W500,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
        return
    }
    val lines = text.split("\n")
    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        lines.forEach { raw ->
            val line = raw.replace(Regex("\\s+$"), "")
            if (line.isBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
            } else {
                val indent = line.takeWhile { it.isWhitespace() }.length
                val body = line.trim()
                if (body.endsWith(":") && body.length <= 64) {
                    Text(body, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Blue, modifier = Modifier.padding(vertical = 2.dp))
                } else {
                    val bullet = Regex("^(•|–|-|\\*)\\s+(.*)").find(body)
                    val numbered = Regex("^(\\d+)[.)]\\s+(.*)").find(body)
                    val spot = Regex("^(.+?)\\s+—\\s+(.*)").find(body)
                    when {
                        bullet != null -> Row(modifier = Modifier.padding(top = 2.dp, start = if (indent >= 2) 6.dp else 0.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("•", fontSize = 14.sp, color = Blue, fontWeight = FontWeight.Bold, lineHeight = 19.sp)
                            Text(bullet.groupValues[2], fontSize = 14.sp, color = DarkText, lineHeight = 19.sp, modifier = Modifier.weight(1f))
                        }
                        numbered != null -> Row(modifier = Modifier.padding(top = 2.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("${numbered.groupValues[1]}.", fontSize = 14.sp, color = Blue, fontWeight = FontWeight.Bold, lineHeight = 19.sp)
                            Text(numbered.groupValues[2], fontSize = 14.sp, color = DarkText, lineHeight = 19.sp, modifier = Modifier.weight(1f))
                        }
                        spot != null && body.contains("•") -> Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = BoldName)) { append(spot.groupValues[1]) }
                                append("  —  ${spot.groupValues[2]}")
                            },
                            fontSize = 14.sp, lineHeight = 19.sp, color = DarkText, modifier = Modifier.padding(top = 4.dp)
                        )
                        indent >= 2 -> Text(body, fontSize = 12.sp, color = SubNote, lineHeight = 16.sp, modifier = Modifier.padding(start = 12.dp))
                        else -> Text(body, fontSize = 14.sp, lineHeight = 19.sp, color = DarkText, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatOverlay(
    onClose: () -> Unit,
    destination: String = "Taal Heritage Town",
    budget: Int = 3500,
    userLocation: Pair<Double, Double> = 13.9550 to 121.1600
) {
    val ctx = remember(destination, budget, userLocation) {
        BrainContext(destination = destination, budget = budget, userLocation = userLocation, spots = SPOTS)
    }
    var messages by remember {
        mutableStateOf(listOf(ChatMessage("bot", "Kamusta! I'm Lakbay AI 🌋 — your Batangas travel assistant. Ask me to plan a trip, find the best food/cafés, check prices, or how far a place is. Tap a suggestion below to start!")))
    }
    var input by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val chips = listOf("Plan my Taal trip", "Best lomi nearby?", "Budget ₱2000")

    fun send(text: String) {
        val clean = text.replace(Regex("[\\u0000-\\u001f]"), " ").replace(Regex("\\s+"), " ").trim().take(500)
        if (clean.isEmpty()) return
        val reply = LakbayBrain.answer(clean, ctx)
        messages = messages + ChatMessage("user", clean) + ChatMessage("bot", reply)
        input = ""
        focusManager.clearFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .clickable(enabled = false, onClick = {}),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌋", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Lakbay AI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F1B3D)
                        )
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = "Close AI chat", tint = Color(0xFF64708A))
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    items(messages) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = if (msg.role == "user") Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (msg.role == "user") Blue else Color(0xFFF1F5FB),
                                modifier = Modifier.widthIn(max = 320.dp)
                            ) {
                                ChatRichText(msg.text, msg.role == "user")
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    chips.forEach { chip ->
                        SuggestionChip(onClick = { send(chip) }, label = { Text(chip, fontSize = 11.sp) })
                    }
                }

                Row(
                    modifier = Modifier
                        .background(Color(0xFFF8FAFC))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("Ask about places, budget, plans…", fontSize = 12.sp) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { send(input) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text("Send", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreen(
    navController: androidx.navigation.NavController,
    destination: String = "Taal Heritage Town",
    budget: Int = 3500,
    userLocation: Pair<Double, Double> = 13.9550 to 121.1600
) {
    ChatOverlay(onClose = { navController.popBackStack() }, destination, budget, userLocation)
}
