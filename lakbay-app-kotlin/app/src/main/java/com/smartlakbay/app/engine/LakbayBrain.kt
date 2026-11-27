package com.smartlakbay.app.engine

import com.smartlakbay.app.models.*

// ---------- Lakbay AI: intelligent conversational engine ----------
// Knows all about Batangas destinations, budgets, distances, app features,
// and responds dynamically to any user query without repeating.

data class BrainContext(
    val destination: String,
    val budget: Int,
    val userLocation: Pair<Double, Double> = 13.9550 to 121.1600,
    val spots: List<Spot> = SPOTS
)

private fun norm2(s: String) = s.lowercase()

private fun spotScore(terms: List<String>, spot: Spot): Boolean {
    val hay = norm2(spot.name + " " + spot.desc + " " + spot.category + " " + spot.icon)
    return terms.any { t -> t.isNotEmpty() && hay.contains(norm2(t)) }
}

object LakbayBrain {

    fun answer(rawQuery: String, ctx: BrainContext): String {
        val q = norm2(rawQuery)
        val spots = ctx.spots
        val userLoc = ctx.userLocation
        val defaultBudget = ctx.budget
        val dest = ctx.destination
        val lines = mutableListOf<String>()

        val matches = { terms: List<String> -> spots.filter { spotScore(terms, it) } }
        fun byRating(list: List<Spot>) = list.sortedByDescending { it.rating }
        fun short(s: Spot, n: Int = 90): String {
            val d = s.desc.trim()
            return if (d.length > n) d.take(n).trimEnd() + "…" else d
        }
        fun line(s: Spot): String {
            val dist = calculateDistance(userLoc.first, userLoc.second, s.lat, s.lng)
            return "${s.icon} ${s.name} — ${s.category} • ${if (s.price == 0) "Free" else peso(s.price)} • ★${s.rating} • ${dist} km away • ${if (s.isOpen) "Open Now" else "Closed"}" +
                (if (s.desc.isNotBlank()) "\n    ${short(s)}" else "")
        }

        // Extract any specific budget mentioned in the query (e.g. "1000 budget", "100 peso", "₱500")
        val budgetRegex = Regex("(?:₱|peso|php|budget|worth|under)?\\s*(\\d{1,5})", RegexOption.IGNORE_CASE)
        val budgetMatch = budgetRegex.findAll(q).mapNotNull { it.groupValues[1].toIntOrNull() }.firstOrNull { it > 5 }
        val activeBudget = budgetMatch ?: defaultBudget

        // 1. Greeting
        if (q.isBlank() || Regex("^(hi|hey|hello|kumusta|kamusta|ganda|good\\s?(morning|afternoon|evening)|maganda)").containsMatchIn(q.trim())) {
            lines.add("Kumusta! I'm Lakbay AI 🌋 — your personal Batangas & Philippine travel assistant.")
            lines.add("I know everything about Smart Lakbay: trip planning, budgets, real-time GPS maps, traveler reviews, and heritage destinations.")
            lines.add("Right now your budget is set at ${peso(activeBudget)}. What would you like to explore today?")
            lines.add("Try asking: \"Plan my Taal trip\", \"Best free trips\", or \"What can I visit with a ₱1000 budget?\".")
            return lines.joinToString("\n")
        }

        // 2. Help / App features knowledge
        if (q.contains("help") || q.contains("app") || q.contains("feature") || q.contains("what can you")) {
            lines.add("Smart Lakbay Features & How I Can Help 🤝:")
            lines.add("• 🗺️ Interactive Map: Real-time GPS location tracking & Get Directions to any establishment.")
            lines.add("• 🎒 Trips Tab: Manage upcoming trips, set dates, reorder itinerary stops, view history, or 'Redo Trip'.")
            lines.add("• ✍️ Community Reviews: Share reviews with photo & video attachments and chat with fellow travelers.")
            lines.add("• 🌐 10 App Languages: Switch between English, Filipino, Chinese, Spanish, Japanese, Korean, French, German, Arabic, & Italian.")
            lines.add("• 💾 Persistent Accounts: All your user changes and saved trips are safely stored in your account folder.")
            lines.add("Just ask me anything in plain words!")
            return lines.joinToString("\n")
        }

        // 3. Free or specific low budget queries ("best free trips", "100 peso budget", etc.)
        if (q.contains("free") || (budgetMatch != null && budgetMatch <= 200)) {
            val freeSpots = byRating(spots.filter { it.price <= activeBudget })
            lines.add("Here are the best spots within your ${peso(activeBudget)} budget ⭐:")
            freeSpots.take(4).forEach { lines.add(line(it)) }
            lines.add("Enjoy these incredible destinations without breaking the bank! Want me to build an itinerary with these?")
            return lines.joinToString("\n")
        }

        // 4. Specific budget query ("1000 budget", "under 2000", etc.)
        if (budgetMatch != null || q.contains("budget") || q.contains("afford") || q.contains("cheapest") || q.contains("tipid")) {
            val affordable = byRating(spots.filter { it.price <= activeBudget })
            lines.add("Within your ${peso(activeBudget)} budget, here are my top recommendations 🎯:")
            affordable.take(4).forEach { lines.add(line(it)) }
            val total = affordable.take(4).sumOf { it.price }
            lines.add("🧾 Total for these top 4: ${peso(total)} (fits your ${peso(activeBudget)} budget ✅).")
            lines.add("Say \"plan it\" if you want to turn these into a saved trip!")
            return lines.joinToString("\n")
        }

        // 5. Best food, lomi, coffee, beaches
        if (q.contains("lomi") || q.contains("food") || q.contains("eat") || q.contains("coffee") || q.contains("cafe") || q.contains("café") || q.contains("beach")) {
            val keyword = when {
                q.contains("lomi") -> "lomi"
                q.contains("coffee") || q.contains("cafe") || q.contains("café") -> "cafe"
                q.contains("beach") -> "beach"
                else -> "food"
            }
            val filtered = spots.filter { norm2(it.name + " " + it.desc + " " + it.category).contains(keyword) }
            val results = if (filtered.isNotEmpty()) byRating(filtered) else byRating(spots.filter { it.category == "food" || it.category == "cafe" })
            lines.add("Here are the best places for $keyword for you 🍲☕:")
            results.take(3).forEach { lines.add(line(it)) }
            lines.add("You can tap 'Get Directions' in the Map tab to visit them directly!")
            return lines.joinToString("\n")
        }

        // 6. Distance / how far
        if (q.contains("how far") || q.contains("distance") || q.contains("km away")) {
            val target = spots.firstOrNull { spotScore(listOf(q.split("to ").lastOrNull() ?: ""), it) } ?: spots.first()
            val d = calculateDistance(userLoc.first, userLoc.second, target.lat, target.lng)
            lines.add("🚗 ${target.name} is about $d km away from your current live GPS location.")
            lines.add("Open the Map tab to view the live route and get instant directions.")
            return lines.joinToString("\n")
        }

        // 7. Plan a trip / itinerary
        if (q.contains("plan") || q.contains("itinerary") || q.contains("trip") || q.contains("schedule")) {
            val chosen = byRating(spots).take(4)
            val total = chosen.sumOf { it.price }
            lines.add("Here is a customized 4-stop itinerary for your trip 🗺️:")
            var t = 9 * 60
            chosen.forEachIndexed { i, s ->
                val hh = "%02d".format(t / 60 % 24)
                val mm = "%02d".format(t % 60)
                lines.add("Stop ${i + 1} ($hh:$mm) ${line(s)}")
                t += 80
            }
            lines.add("🧾 Total estimated fees: ${peso(total)} (within your ${peso(activeBudget)} budget ✅).")
            lines.add("You can save this trip to your Trips tab and customize the time and order anytime!")
            return lines.joinToString("\n")
        }

        // 8. Specific place search
        val found = spots.filter { s -> norm2(s.name).contains(q) || norm2(s.category).contains(q) }
        if (found.isNotEmpty()) {
            lines.add("I found these places matching your request 🎯:")
            byRating(found).take(3).forEach { lines.add(line(it)) }
            return lines.joinToString("\n")
        }

        // 9. Intelligent fallback with varied suggestions
        lines.add("That's a great question about traveling in Batangas! 🌋")
        lines.add("With your budget of ${peso(activeBudget)}, I recommend visiting Marian Orchard, The Outlets at LIMA, or enjoying authentic Batangas Kapeng Barako at Café de Lipa.")
        lines.add("You can ask me to plan a trip, find food/cafés, check prices, or view distances.")
        return lines.joinToString("\n")
    }
}
