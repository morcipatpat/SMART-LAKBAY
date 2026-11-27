package com.smartlakbay.app.models

import android.content.Context

data class Spot(
    val key: String,
    val name: String,
    val category: String,
    val price: Int,
    val rating: Double,
    val lat: Double,
    val lng: Double,
    val isOpen: Boolean,
    val icon: String,
    val desc: String
)

data class Destination(
    val name: String,
    val dist: String,
    val icon: String,
    val town: String
)

data class Trip(
    val id: String,
    val name: String,
    val meta: String,
    val date: String,
    val done: Boolean
)

data class Review(
    val id: String,
    val owner: String,
    val author: String,
    val tripName: String,
    val rating: Int,
    val text: String,
    val date: String,
    val media: List<String>,
    val comments: List<Comment>
)

data class Comment(
    val id: String,
    val author: String,
    val text: String
)

/**
 * Persisted demo accounts (mirrors the localStorage accounts in the original
 * React Native app).
 */
class DemoAccounts(private val context: Context) {
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

// ---------- Ported from App.js SPOTS ----------
val SPOTS = listOf(
    // Batangas & Nearby
    Spot("marianOrchard", "Marian Orchard Pilgrimage Center", "heritage", 100, 4.9, 14.0142, 121.1001, true, "⛪", "Spiritual park and garden in Malabanan, Balete, Batangas."),
    Spot("outletsLima", "The Outlets at LIMA Estate", "store", 0, 4.8, 13.9820, 121.1550, true, "🛍️", "Popular open-air shopping mall estate in Lipa/Malvar."),
    Spot("srLipa", "S&R Membership Shopping Lipa", "store", 500, 4.7, 13.9450, 121.1580, true, "🛒", "Membership shopping warehouse."),
    Spot("smLipa", "SM City Lipa", "store", 0, 4.6, 13.9535, 121.1633, true, "🏬", "Premier shopping mall in Lipa City."),
    Spot("cafeDeLipa", "Café de Lipa • Original Barako", "cafe", 180, 4.8, 13.9412, 121.1620, true, "☕", "Famous authentic Batangas kapeng barako and pastries."),
    Spot("oldGrove", "The Old Grove Farmstead", "heritage", 350, 4.6, 13.9785, 121.1215, true, "🚜", "Scenic farmstead with Dutch windmill and farm animals."),
    Spot("donJuanLipa", "Don Juan BBQ & Lomi", "food", 250, 4.7, 13.9419, 121.1644, true, "🍲", "Top-rated Batangas lomi and grilled barbecue."),
    Spot("darasaMalvar", "Darasa & Malvar Highway (E2)", "heritage", 0, 4.8, 14.0520, 121.1450, true, "🛣️", "Major highway route passing Darasa and Malvar towards E2."),
    Spot("bulacnin", "Bulacnin Northwest Loop", "heritage", 0, 4.7, 14.0150, 121.1320, true, "📍", "Scenic route looping northwest through Bulacnin."),
    Spot("summitPoint", "Summit Point Golf and Country Club", "heritage", 500, 4.9, 13.9920, 121.1280, true, "⛳", "Premier golf and country club near Inosloban and Marauoy."),
    Spot("homeDestination", "Home (Destination)", "store", 0, 5.0, 13.9550, 121.1600, true, "🏠", "Your home destination in Lipa / Dagatan / Marauoy area."),

    // Baguio & Benguet
    Spot("baguioCathedral", "Baguio Cathedral (Our Lady of Atonement)", "heritage", 0, 4.9, 16.4123, 120.5977, true, "⛪", "Iconic pink cathedral atop Session Road in Baguio City."),
    Spot("cafeByTheRuins", "Café by the Ruins", "cafe", 350, 4.8, 16.4140, 120.5940, true, "☕", "Artistic legendary dining spot serving Cordillera-inspired cuisine."),
    Spot("smBaguio", "SM City Baguio", "store", 0, 4.7, 16.4095, 120.5985, true, "🏬", "Open-air mall overlooking Burnham Park."),
    Spot("campJohnHay", "Camp John Hay Eco-Trail & Resort", "heritage", 100, 4.9, 16.3980, 120.6120, true, "🌲", "Pine tree sanctuary, restaurants, and historical resort in Baguio."),

    // Tagaytay & Cavite
    Spot("peoplesPark", "People's Park in the Sky", "heritage", 50, 4.5, 14.1450, 121.0110, true, "⛰️", "Historical urban park overlooking Taal Lake and Volcano."),
    Spot("twinLakesTagaytay", "Twin Lakes Tagaytay Starbucks", "cafe", 250, 4.8, 14.0320, 120.9100, true, "☕", "Scenic European-style estate with breathtaking cliffside views of Taal."),

    // Pampanga & Central Luzon
    Spot("smPampanga", "SM City Pampanga & Giant Ferris Wheel", "store", 0, 4.7, 15.0480, 120.6900, true, "🎡", "Major shopping center along MacArthur Highway."),
    Spot("bourbonSisig", "Aling Lucing's Sisig (Angeles)", "food", 200, 4.9, 15.1450, 120.5890, true, "🍳", "The birthplace of authentic sizzling pork sisig in Angeles City."),

    // Ilocos & Northern Luzon
    Spot("calleCrisologo", "Calle Crisologo (Vigan Heritage Village)", "heritage", 0, 4.9, 17.5747, 120.3867, true, "🏮", "UNESCO World Heritage cobblestone street featuring Spanish colonial houses."),

    // La Union
    Spot("sanJuanSurf", "San Juan Surf Resort & Beach", "heritage", 0, 4.8, 16.6710, 120.3340, true, "🏄", "The surfing capital of Northern Luzon in La Union."),
    Spot("flotsamHostel", "Flotsam and Jetsam Hostel Cafe", "cafe", 300, 4.7, 16.6650, 120.3350, true, "🍹", "Vibrant bohemian art hostel and café in San Juan."),

    // Bicol
    Spot("cagsawaRuins", "Cagsawa Ruins & Mayon Volcano View", "heritage", 50, 4.9, 13.1550, 123.7020, true, "🌋", "16th-century Franciscan church bell tower with majestic Mayon Volcano backdrop."),

    // Metro Manila / NCR
    Spot("intramurosManila", "Intramuros & Fort Santiago", "heritage", 75, 4.8, 14.5937, 120.9755, true, "🏰", "Historic walled city in Manila."),
    Spot("bgcHighStreet", "Bonifacio High Street (BGC)", "store", 0, 4.9, 14.5528, 121.0505, true, "🌆", "Premier lifestyle and shopping district in Taguig."),
    Spot("mallOfAsia", "SM Mall of Asia (MOA)", "store", 0, 4.8, 14.5350, 120.9820, true, "🎡", "Massive seaside shopping and entertainment mall in Pasay.")
)

val DESTINATIONS = listOf(
    Destination("Taal Basilica", "Taal, Batangas • 35 min", "⛪", "Taal Heritage Town"),
    Destination("Café de Lipa", "Lipa City • Heritage District", "☕", "Lipa Heritage District"),
    Destination("Nasugbu Beach Resorts", "Nasugbu • 90 min", "🏖️", "Nasugbu"),
    Destination("Baguio Cathedral", "Baguio City • 3.5 hrs", "⛪", "Baguio City"),
    Destination("Camp John Hay", "Baguio • Pine Sanctuary", "🌲", "Baguio City"),
    Destination("People's Park in the Sky", "Tagaytay • Ridge View", "⛰️", "Tagaytay"),
    Destination("Twin Lakes Tagaytay", "Tagaytay-Nasugbu Hwy", "☕", "Tagaytay"),
    Destination("Calle Crisologo", "Vigan, Ilocos Sur • Heritage", "🏮", "Vigan"),
    Destination("San Juan Surf Beach", "La Union • Surfing Capital", "🏄", "La Union"),
    Destination("Cagsawa Ruins", "Albay, Bicol • Mayon View", "🌋", "Bicol"),
    Destination("Intramuros Manila", "Manila • Walled City", "🏰", "Manila"),
    Destination("BGC High Street", "Taguig • Lifestyle Hub", "🌆", "Taguig")
)

val DEST_SPOT_KEYWORDS = mapOf(
    "Taal Heritage Town" to listOf("Taal", "Marian Orchard", "barako", "pilgrim", "Basilica"),
    "Lipa Heritage District" to listOf("Lipa", "barako", "lomi", "Don Juan", "Outlets", "S&R", "SM City Lipa", "The Old Grove", "Bulacnin", "Summit"),
    "Nasugbu" to listOf("Nasugbu", "beach", "coast"),
    "Baguio City" to listOf("Baguio", "Cathedral", "Camp John Hay", "Café by the Ruins", "SM City Baguio", "Burnham", "pine"),
    "Tagaytay" to listOf("Tagaytay", "People", "People's", "Twin Lakes", "Taal Lake", "vineyard", "volcano"),
    "Vigan" to listOf("Vigan", "Crisologo", "Ilocos", "heritage"),
    "La Union" to listOf("La Union", "San Juan", "Surf", "Flotsam"),
    "Bicol" to listOf("Bicol", "Cagsawa", "Mayon", "Albay", "volcano"),
    "Manila" to listOf("Manila", "Intramuros", "Fort Santiago"),
    "Taguig" to listOf("Taguig", "BGC", "Bonifacio", "High Street")
)

fun peso(n: Int) = "₱" + n.toString()

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2))
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return Math.round(r * c * 10) / 10.0
}
