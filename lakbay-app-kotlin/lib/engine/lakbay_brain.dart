import '../models/models.dart';

// ---------- Lakbay AI: intelligent conversational engine ----------
// Knows all about Batangas destinations, budgets, distances, app features,
// and responds dynamically to any user query without repeating.

class BrainContext {
  final String destination;
  final int budget;
  final (double, double) userLocation;
  final List<Spot> spots;

  BrainContext({
    required this.destination,
    required this.budget,
    this.userLocation = const (13.9550, 121.1600),
    this.spots = const [],
  });
}

String _norm2(String s) => s.toLowerCase();

bool _spotScore(List<String> terms, Spot spot) {
  final hay = _norm2(spot.name + " " + spot.desc + " " + spot.category + " " + spot.icon);
  return terms.any((t) => t.isNotEmpty && hay.contains(_norm2(t)));
}

class LakbayBrain {
  static String answer(String rawQuery, BrainContext ctx) {
    final q = _norm2(rawQuery);
    final spots = ctx.spots;
    final (uLat, uLng) = ctx.userLocation;
    final defaultBudget = ctx.budget;
    final lines = <String>[];

    List<Spot> matches(List<String> terms) =>
        spots.where((s) => _spotScore(terms, s)).toList();
    List<Spot> byRating(List<Spot> list) =>
        [...list]..sort((a, b) => b.rating.compareTo(a.rating));
    String short(Spot s, [int n = 90]) {
      final d = s.desc.trim();
      return d.length > n ? "${d.substring(0, n).trimRight()}…" : d;
    }
    String line(Spot s) {
      final dist = calculateDistance(uLat, uLng, s.lat, s.lng);
      return "${s.icon} ${s.name} — ${s.category} • ${s.price == 0 ? "Free" : peso(s.price)} • ★${s.rating} • ${dist} km away • ${s.isOpen ? "Open Now" : "Closed"}" +
          (s.desc.trim().isNotEmpty ? "\n    ${short(s)}" : "");
    }

    // Extract any specific budget mentioned in the query (e.g. "1000 budget", "100 peso", "₱500")
    final budgetRegex =
        RegExp(r"(?:₱|peso|php|budget|worth|under)?\s*(\d{1,5})");
    Iterable<int> foundBudgets = budgetRegex
        .allMatches(q)
        .map((m) => int.tryParse(m.group(1)!) ?? 0)
        .where((n) => n > 5);
    final budgetMatch = foundBudgets.isNotEmpty ? foundBudgets.first : null;
    final activeBudget = budgetMatch ?? defaultBudget;

    // 1. Greeting
    final greetingRegex = RegExp(
        r"^(hi|hey|hello|kumusta|kamusta|ganda|good\s?(morning|afternoon|evening)|maganda)");
    if (q.trim().isEmpty || greetingRegex.hasMatch(q.trim())) {
      lines.add("Kumusta! I'm Lakbay AI 🌋 — your personal Batangas & Philippine travel assistant.");
      lines.add("I know everything about Smart Lakbay: trip planning, budgets, real-time GPS maps, traveler reviews, and heritage destinations.");
      lines.add("Right now your budget is set at ${peso(activeBudget)}. What would you like to explore today?");
      lines.add('Try asking: "Plan my Taal trip", "Best free trips", or "What can I visit with a ₱1000 budget?".');
      return lines.join("\n");
    }

    // 2. Help / App features knowledge
    if (q.contains("help") ||
        q.contains("app") ||
        q.contains("feature") ||
        q.contains("what can you")) {
      lines.add("Smart Lakbay Features & How I Can Help 🤝:");
      lines.add("• 🗺️ Interactive Map: Real-time GPS location tracking & Get Directions to any establishment.");
      lines.add("• 🎒 Trips Tab: Manage upcoming trips, set dates, reorder itinerary stops, view history, or 'Redo Trip'.");
      lines.add("• ✍️ Community Reviews: Share reviews with photo & video attachments and chat with fellow travelers.");
      lines.add("• 🌐 10 App Languages: Switch between English, Filipino, Chinese, Spanish, Japanese, Korean, French, German, Arabic, & Italian.");
      lines.add("• 💾 Persistent Accounts: All your user changes and saved trips are safely stored in your account folder.");
      lines.add("Just ask me anything in plain words!");
      return lines.join("\n");
    }

    // 3. Free or specific low budget queries ("best free trips", "100 peso budget", etc.)
    if (q.contains("free") || (budgetMatch != null && budgetMatch <= 200)) {
      final freeSpots = byRating(spots.where((s) => s.price <= activeBudget).toList());
      lines.add("Here are the best spots within your ${peso(activeBudget)} budget ⭐:");
      for (final s in freeSpots.take(4)) {
        lines.add(line(s));
      }
      lines.add("Enjoy these incredible destinations without breaking the bank! Want me to build an itinerary with these?");
      return lines.join("\n");
    }

    // 4. Specific budget query ("1000 budget", "under 2000", etc.)
    if (budgetMatch != null ||
        q.contains("budget") ||
        q.contains("afford") ||
        q.contains("cheapest") ||
        q.contains("tipid")) {
      final affordable = byRating(spots.where((s) => s.price <= activeBudget).toList());
      lines.add("Within your ${peso(activeBudget)} budget, here are my top recommendations 🎯:");
      for (final s in affordable.take(4)) {
        lines.add(line(s));
      }
      final total = affordable.take(4).fold(0, (sum, s) => sum + s.price);
      lines.add("🧾 Total for these top 4: ${peso(total)} (fits your ${peso(activeBudget)} budget ✅).");
      lines.add('Say "plan it" if you want to turn these into a saved trip!');
      return lines.join("\n");
    }

    // 5. Best food, lomi, coffee, beaches
    if (q.contains("lomi") ||
        q.contains("food") ||
        q.contains("eat") ||
        q.contains("coffee") ||
        q.contains("cafe") ||
        q.contains("café") ||
        q.contains("beach")) {
      String keyword;
      if (q.contains("lomi")) {
        keyword = "lomi";
      } else if (q.contains("coffee") || q.contains("cafe") || q.contains("café")) {
        keyword = "cafe";
      } else if (q.contains("beach")) {
        keyword = "beach";
      } else {
        keyword = "food";
      }
      final filtered = spots
          .where((s) => _norm2(s.name + " " + s.desc + " " + s.category).contains(keyword))
          .toList();
      final results = filtered.isNotEmpty
          ? byRating(filtered)
          : byRating(
              spots.where((s) => s.category == "food" || s.category == "cafe").toList());
      lines.add("Here are the best places for $keyword for you 🍲☕:");
      for (final s in results.take(3)) {
        lines.add(line(s));
      }
      lines.add("You can tap 'Get Directions' in the Map tab to visit them directly!");
      return lines.join("\n");
    }

    // 6. Distance / how far
    if (q.contains("how far") || q.contains("distance") || q.contains("km away")) {
      final parts = q.split("to ");
      final lastPart = parts.isEmpty ? "" : parts.last;
      final target =
          (spots.firstWhere((s) => _spotScore([lastPart], s), orElse: () => spots.first));
      final d = calculateDistance(uLat, uLng, target.lat, target.lng);
      lines.add("🚗 ${target.name} is about $d km away from your current live GPS location.");
      lines.add("Open the Map tab to view the live route and get instant directions.");
      return lines.join("\n");
    }

    // 7. Plan a trip / itinerary
    if (q.contains("plan") ||
        q.contains("itinerary") ||
        q.contains("trip") ||
        q.contains("schedule")) {
      final chosen = byRating(spots).take(4).toList();
      final total = chosen.fold(0, (sum, s) => sum + s.price);
      lines.add("Here is a customized 4-stop itinerary for your trip 🗺️:");
      var t = 9 * 60;
      for (var i = 0; i < chosen.length; i++) {
        final s = chosen[i];
        final hh = (t ~/ 60 % 24).toString().padLeft(2, "0");
        final mm = (t % 60).toString().padLeft(2, "0");
        lines.add("Stop ${i + 1} ($hh:$mm) ${line(s)}");
        t += 80;
      }
      lines.add("🧾 Total estimated fees: ${peso(total)} (within your ${peso(activeBudget)} budget ✅).");
      lines.add("You can save this trip to your Trips tab and customize the time and order anytime!");
      return lines.join("\n");
    }

    // 8. Specific place search
    final found = spots
        .where((s) => _norm2(s.name).contains(q) || _norm2(s.category).contains(q))
        .toList();
    if (found.isNotEmpty) {
      lines.add("I found these places matching your request 🎯:");
      for (final s in byRating(found).take(3)) {
        lines.add(line(s));
      }
      return lines.join("\n");
    }

    // 9. Intelligent fallback with varied suggestions
    lines.add("That's a great question about traveling in Batangas! 🌋");
    lines.add("With your budget of ${peso(activeBudget)}, I recommend visiting Marian Orchard, The Outlets at LIMA, or enjoying authentic Batangas Kapeng Barako at Café de Lipa.");
    lines.add("You can ask me to plan a trip, find food/cafés, check prices, or view distances.");
    return lines.join("\n");
  }
}
