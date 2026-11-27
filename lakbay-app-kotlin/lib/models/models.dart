import 'dart:math' as math;
import 'package:shared_preferences/shared_preferences.dart';

class Spot {
  final String key;
  final String name;
  final String category;
  final int price;
  final double rating;
  final double lat;
  final double lng;
  final bool isOpen;
  final String icon;
  final String desc;

  const Spot({
    required this.key,
    required this.name,
    required this.category,
    required this.price,
    required this.rating,
    required this.lat,
    required this.lng,
    required this.isOpen,
    required this.icon,
    required this.desc,
  });
}

class Destination {
  final String name;
  final String dist;
  final String icon;
  final String town;

  const Destination({
    required this.name,
    required this.dist,
    required this.icon,
    required this.town,
  });
}

class Trip {
  final String id;
  final String name;
  final String meta;
  final String date;
  final bool done;

  const Trip({
    required this.id,
    required this.name,
    required this.meta,
    required this.date,
    required this.done,
  });
}

class SavedTrip {
  final String id;
  final String name;
  final String date;
  final String meta;
  final bool done;
  final String destination;
  final int budget;
  final String spotKeys;

  SavedTrip({
    required this.id,
    required this.name,
    required this.date,
    required this.meta,
    required this.done,
    required this.destination,
    required this.budget,
    required this.spotKeys,
  });

  SavedTrip copyWith({
    String? id,
    String? name,
    String? date,
    String? meta,
    bool? done,
    String? destination,
    int? budget,
    String? spotKeys,
  }) {
    return SavedTrip(
      id: id ?? this.id,
      name: name ?? this.name,
      date: date ?? this.date,
      meta: meta ?? this.meta,
      done: done ?? this.done,
      destination: destination ?? this.destination,
      budget: budget ?? this.budget,
      spotKeys: spotKeys ?? this.spotKeys,
    );
  }
}

class CommunityComment {
  final String id;
  final String author;
  final String text;

  CommunityComment({
    required this.id,
    required this.author,
    required this.text,
  });
}

class CommunityReview {
  final String id;
  final String author;
  final String tripName;
  final String text;
  final String date;
  final int rating;
  final String? mediaType;
  final String? mediaResName;
  final List<CommunityComment> comments;

  CommunityReview({
    required this.id,
    required this.author,
    required this.tripName,
    required this.text,
    required this.date,
    this.rating = 5,
    this.mediaType,
    this.mediaResName,
    List<CommunityComment>? comments,
  }) : comments = comments ?? [];
}

class AppNotification {
  final String id;
  final String title;
  final String message;
  final String time;
  final String icon;
  final String type;

  const AppNotification({
    required this.id,
    required this.title,
    required this.message,
    required this.time,
    required this.icon,
    required this.type,
  });
}

class LangOption {
  final String code;
  final String name;
  final String native;
  final String tag;

  const LangOption({
    required this.code,
    required this.name,
    required this.native,
    required this.tag,
  });
}

class DestinationImages {
  final String main;
  final List<String> views;

  const DestinationImages({
    required this.main,
    required this.views,
  });
}

DestinationImages getDestinationImages(String dest) {
  if (dest.contains("Taal")) {
    return const DestinationImages(
      main: "assets/destinations/taal.jpg",
      views: [
        "assets/destinations/taal_2.jpg",
        "assets/destinations/taal_3.jpg",
        "assets/destinations/taal_4.jpg",
      ],
    );
  } else if (dest.contains("Camp John Hay")) {
    return const DestinationImages(
      main: "assets/destinations/camp_john_hay.jpg",
      views: [
        "assets/destinations/camp_john_hay_2.jpg",
        "assets/destinations/camp_john_hay_3.jpg",
        "assets/destinations/camp_john_hay_4.jpg",
      ],
    );
  } else if (dest.contains("Baguio Cathedral") || dest.contains("Baguio")) {
    return const DestinationImages(
      main: "assets/destinations/baguio_cathedral.jpg",
      views: [
        "assets/destinations/baguio_cathedral_2.jpg",
        "assets/destinations/baguio_cathedral_3.jpg",
        "assets/destinations/baguio_cathedral_4.jpg",
      ],
    );
  } else if (dest.contains("Nasugbu")) {
    return const DestinationImages(
      main: "assets/destinations/nasugbu.jpg",
      views: [
        "assets/destinations/nasugbu_2.jpg",
        "assets/destinations/nasugbu_3.jpg",
        "assets/destinations/nasugbu_5.jpg",
      ],
    );
  } else if (dest.contains("People") || dest.contains("Tagaytay")) {
    return const DestinationImages(
      main: "assets/destinations/peoples_park.jpg",
      views: [
        "assets/destinations/peoples_park_2.jpg",
        "assets/destinations/peoples_park_3.jpg",
        "assets/destinations/peoples_park_4.jpg",
      ],
    );
  } else if (dest.contains("Twin Lakes")) {
    return const DestinationImages(
      main: "assets/destinations/twin_lakes.jpg",
      views: [
        "assets/destinations/twin_lakes_2.jpg",
        "assets/destinations/twin_lakes_3.jpg",
      ],
    );
  } else if (dest.contains("Lipa")) {
    return const DestinationImages(
      main: "assets/destinations/cafe_de_lipa.jpg",
      views: [
        "assets/destinations/cafe_de_lipa_2.jpg",
        "assets/destinations/cafe_de_lipa_3.jpg",
        "assets/destinations/cafe_de_lipa_4.jpg",
      ],
    );
  } else if (dest.contains("Vigan") || dest.contains("Crisologo")) {
    return const DestinationImages(
      main: "assets/destinations/calle_crisologo.jpg",
      views: [
        "assets/destinations/calle_crisologo_2.jpg",
        "assets/destinations/calle_crisologo_3.jpg",
        "assets/destinations/calle_crisologo_4.jpg",
      ],
    );
  } else if (dest.contains("La Union") || dest.contains("San Juan")) {
    return const DestinationImages(
      main: "assets/destinations/san_juan_beach.jpg",
      views: [
        "assets/destinations/san_juan_beach_2.jpg",
        "assets/destinations/san_juan_beach_3.jpg",
        "assets/destinations/san_juan_beach_4.jpg",
      ],
    );
  } else if (dest.contains("Bicol") || dest.contains("Cagsawa")) {
    return const DestinationImages(
      main: "assets/destinations/cagsawa_ruins.jpg",
      views: [
        "assets/destinations/cagsawa_ruins_2.jpg",
      ],
    );
  } else if (dest.contains("Manila") || dest.contains("Intramuros")) {
    return const DestinationImages(
      main: "assets/destinations/intramuros.jpg",
      views: [
        "assets/destinations/intramuros_2.jpg",
        "assets/destinations/intramuros_3.jpg",
        "assets/destinations/intramuros_4.jpg",
      ],
    );
  } else if (dest.contains("Taguig") || dest.contains("BGC")) {
    return const DestinationImages(
      main: "assets/destinations/bgc.jpg",
      views: [
        "assets/destinations/bgc_2.jpg",
        "assets/destinations/bgc_3.jpg",
        "assets/destinations/bgc_4.jpg",
      ],
    );
  }
  return const DestinationImages(
    main: "assets/destinations/default.jpg",
    views: [],
  );
}

class DemoAccounts {
  static const String _key = "smart_lakbay_accounts_list";

  static Future<List<MapEntry<String, String>>> all() async {
    final prefs = await SharedPreferences.getInstance();
    final raw = prefs.getString(_key);
    if (raw == null || raw.isEmpty) return [];
    return raw
        .split("\n")
        .where((line) => line.trim().isNotEmpty)
        .map((line) {
          final p = line.split("|");
          return MapEntry(p[0], p.length > 1 ? p[1] : "");
        })
        .toList();
  }

  static Future<MapEntry<String, String>?> find(String email) async {
    final list = await all();
    for (final entry in list) {
      if (entry.key.toLowerCase() == email.toLowerCase()) {
        return entry;
      }
    }
    return null;
  }

  static Future<void> add(String email, String password) async {
    final prefs = await SharedPreferences.getInstance();
    final list = await all();
    final updated = [...list, MapEntry(email, password)];
    await prefs.setString(
      _key,
      updated.map((e) => "${e.key}|${e.value}").join("\n"),
    );
  }
}

const List<Spot> SPOTS = [
  // Batangas & Nearby
  Spot(
    key: "marianOrchard",
    name: "Marian Orchard Pilgrimage Center",
    category: "heritage",
    price: 100,
    rating: 4.9,
    lat: 14.0142,
    lng: 121.1001,
    isOpen: true,
    icon: "⛪",
    desc: "Spiritual park and garden in Malabanan, Balete, Batangas.",
  ),
  Spot(
    key: "outletsLima",
    name: "The Outlets at LIMA Estate",
    category: "store",
    price: 0,
    rating: 4.8,
    lat: 13.9820,
    lng: 121.1550,
    isOpen: true,
    icon: "🛍️",
    desc: "Popular open-air shopping mall estate in Lipa/Malvar.",
  ),
  Spot(
    key: "srLipa",
    name: "S&R Membership Shopping Lipa",
    category: "store",
    price: 500,
    rating: 4.7,
    lat: 13.9450,
    lng: 121.1580,
    isOpen: true,
    icon: "🛒",
    desc: "Membership shopping warehouse.",
  ),
  Spot(
    key: "smLipa",
    name: "SM City Lipa",
    category: "store",
    price: 0,
    rating: 4.6,
    lat: 13.9535,
    lng: 121.1633,
    isOpen: true,
    icon: "🏬",
    desc: "Premier shopping mall in Lipa City.",
  ),
  Spot(
    key: "cafeDeLipa",
    name: "Café de Lipa • Original Barako",
    category: "cafe",
    price: 180,
    rating: 4.8,
    lat: 13.9412,
    lng: 121.1620,
    isOpen: true,
    icon: "☕",
    desc: "Famous authentic Batangas kapeng barako and pastries.",
  ),
  Spot(
    key: "oldGrove",
    name: "The Old Grove Farmstead",
    category: "heritage",
    price: 350,
    rating: 4.6,
    lat: 13.9785,
    lng: 121.1215,
    isOpen: true,
    icon: "🚜",
    desc: "Scenic farmstead with Dutch windmill and farm animals.",
  ),
  Spot(
    key: "donJuanLipa",
    name: "Don Juan BBQ & Lomi",
    category: "food",
    price: 250,
    rating: 4.7,
    lat: 13.9419,
    lng: 121.1644,
    isOpen: true,
    icon: "🍲",
    desc: "Top-rated Batangas lomi and grilled barbecue.",
  ),
  Spot(
    key: "darasaMalvar",
    name: "Darasa & Malvar Highway (E2)",
    category: "heritage",
    price: 0,
    rating: 4.8,
    lat: 14.0520,
    lng: 121.1450,
    isOpen: true,
    icon: "🛣️",
    desc: "Major highway route passing Darasa and Malvar towards E2.",
  ),
  Spot(
    key: "bulacnin",
    name: "Bulacnin Northwest Loop",
    category: "heritage",
    price: 0,
    rating: 4.7,
    lat: 14.0150,
    lng: 121.1320,
    isOpen: true,
    icon: "📍",
    desc: "Scenic route looping northwest through Bulacnin.",
  ),
  Spot(
    key: "summitPoint",
    name: "Summit Point Golf and Country Club",
    category: "heritage",
    price: 500,
    rating: 4.9,
    lat: 13.9920,
    lng: 121.1280,
    isOpen: true,
    icon: "⛳",
    desc: "Premier golf and country club near Inosloban and Marauoy.",
  ),
  Spot(
    key: "homeDestination",
    name: "Home (Destination)",
    category: "store",
    price: 0,
    rating: 5.0,
    lat: 13.9550,
    lng: 121.1600,
    isOpen: true,
    icon: "🏠",
    desc: "Your home destination in Lipa / Dagatan / Marauoy area.",
  ),

  // Baguio & Benguet
  Spot(
    key: "baguioCathedral",
    name: "Baguio Cathedral (Our Lady of Atonement)",
    category: "heritage",
    price: 0,
    rating: 4.9,
    lat: 16.4123,
    lng: 120.5977,
    isOpen: true,
    icon: "⛪",
    desc: "Iconic pink cathedral atop Session Road in Baguio City.",
  ),
  Spot(
    key: "cafeByTheRuins",
    name: "Café by the Ruins",
    category: "cafe",
    price: 350,
    rating: 4.8,
    lat: 16.4140,
    lng: 120.5940,
    isOpen: true,
    icon: "☕",
    desc: "Artistic legendary dining spot serving Cordillera-inspired cuisine.",
  ),
  Spot(
    key: "smBaguio",
    name: "SM City Baguio",
    category: "store",
    price: 0,
    rating: 4.7,
    lat: 16.4095,
    lng: 120.5985,
    isOpen: true,
    icon: "🏬",
    desc: "Open-air mall overlooking Burnham Park.",
  ),
  Spot(
    key: "campJohnHay",
    name: "Camp John Hay Eco-Trail & Resort",
    category: "heritage",
    price: 100,
    rating: 4.9,
    lat: 16.3980,
    lng: 120.6120,
    isOpen: true,
    icon: "🌲",
    desc: "Pine tree sanctuary, restaurants, and historical resort in Baguio.",
  ),

  // Tagaytay & Cavite
  Spot(
    key: "peoplesPark",
    name: "People's Park in the Sky",
    category: "heritage",
    price: 50,
    rating: 4.5,
    lat: 14.1450,
    lng: 121.0110,
    isOpen: true,
    icon: "⛰️",
    desc: "Historical urban park overlooking Taal Lake and Volcano.",
  ),
  Spot(
    key: "twinLakesTagaytay",
    name: "Twin Lakes Tagaytay Starbucks",
    category: "cafe",
    price: 250,
    rating: 4.8,
    lat: 14.0320,
    lng: 120.9100,
    isOpen: true,
    icon: "☕",
    desc: "Scenic European-style estate with breathtaking cliffside views of Taal.",
  ),

  // Pampanga & Central Luzon
  Spot(
    key: "smPampanga",
    name: "SM City Pampanga & Giant Ferris Wheel",
    category: "store",
    price: 0,
    rating: 4.7,
    lat: 15.0480,
    lng: 120.6900,
    isOpen: true,
    icon: "🎡",
    desc: "Major shopping center along MacArthur Highway.",
  ),
  Spot(
    key: "bourbonSisig",
    name: "Aling Lucing's Sisig (Angeles)",
    category: "food",
    price: 200,
    rating: 4.9,
    lat: 15.1450,
    lng: 120.5890,
    isOpen: true,
    icon: "🍳",
    desc: "The birthplace of authentic sizzling pork sisig in Angeles City.",
  ),

  // Ilocos & Northern Luzon
  Spot(
    key: "calleCrisologo",
    name: "Calle Crisologo (Vigan Heritage Village)",
    category: "heritage",
    price: 0,
    rating: 4.9,
    lat: 17.5747,
    lng: 120.3867,
    isOpen: true,
    icon: "🏮",
    desc: "UNESCO World Heritage cobblestone street featuring Spanish colonial houses.",
  ),

  // La Union
  Spot(
    key: "sanJuanSurf",
    name: "San Juan Surf Resort & Beach",
    category: "heritage",
    price: 0,
    rating: 4.8,
    lat: 16.6710,
    lng: 120.3340,
    isOpen: true,
    icon: "🏄",
    desc: "The surfing capital of Northern Luzon in La Union.",
  ),
  Spot(
    key: "flotsamHostel",
    name: "Flotsam and Jetsam Hostel Cafe",
    category: "cafe",
    price: 300,
    rating: 4.7,
    lat: 16.6650,
    lng: 120.3350,
    isOpen: true,
    icon: "🍹",
    desc: "Vibrant bohemian art hostel and café in San Juan.",
  ),

  // Bicol
  Spot(
    key: "cagsawaRuins",
    name: "Cagsawa Ruins & Mayon Volcano View",
    category: "heritage",
    price: 50,
    rating: 4.9,
    lat: 13.1550,
    lng: 123.7020,
    isOpen: true,
    icon: "🌋",
    desc: "16th-century Franciscan church bell tower with majestic Mayon Volcano backdrop.",
  ),

  // Metro Manila / NCR
  Spot(
    key: "intramurosManila",
    name: "Intramuros & Fort Santiago",
    category: "heritage",
    price: 75,
    rating: 4.8,
    lat: 14.5937,
    lng: 120.9755,
    isOpen: true,
    icon: "🏰",
    desc: "Historic walled city in Manila.",
  ),
  Spot(
    key: "bgcHighStreet",
    name: "Bonifacio High Street (BGC)",
    category: "store",
    price: 0,
    rating: 4.9,
    lat: 14.5528,
    lng: 121.0505,
    isOpen: true,
    icon: "🌆",
    desc: "Premier lifestyle and shopping district in Taguig.",
  ),
  Spot(
    key: "mallOfAsia",
    name: "SM Mall of Asia (MOA)",
    category: "store",
    price: 0,
    rating: 4.8,
    lat: 14.5350,
    lng: 120.9820,
    isOpen: true,
    icon: "🎡",
    desc: "Massive seaside shopping and entertainment mall in Pasay.",
  ),
];

const List<Destination> DESTINATIONS = [
  Destination(name: "Taal Basilica", dist: "Taal, Batangas • 35 min", icon: "⛪", town: "Taal Heritage Town"),
  Destination(name: "Café de Lipa", dist: "Lipa City • Heritage District", icon: "☕", town: "Lipa Heritage District"),
  Destination(name: "Nasugbu Beach Resorts", dist: "Nasugbu • 90 min", icon: "🏖️", town: "Nasugbu"),
  Destination(name: "Baguio Cathedral", dist: "Baguio City • 3.5 hrs", icon: "⛪", town: "Baguio City"),
  Destination(name: "Camp John Hay", dist: "Baguio • Pine Sanctuary", icon: "🌲", town: "Baguio City"),
  Destination(name: "People's Park in the Sky", dist: "Tagaytay • Ridge View", icon: "⛰️", town: "Tagaytay"),
  Destination(name: "Twin Lakes Tagaytay", dist: "Tagaytay-Nasugbu Hwy", icon: "☕", town: "Tagaytay"),
  Destination(name: "Calle Crisologo", dist: "Vigan, Ilocos Sur • Heritage", icon: "🏮", town: "Vigan"),
  Destination(name: "San Juan Surf Beach", dist: "La Union • Surfing Capital", icon: "🏄", town: "La Union"),
  Destination(name: "Cagsawa Ruins", dist: "Albay, Bicol • Mayon View", icon: "🌋", town: "Bicol"),
  Destination(name: "Intramuros Manila", dist: "Manila • Walled City", icon: "🏰", town: "Manila"),
  Destination(name: "BGC High Street", dist: "Taguig • Lifestyle Hub", icon: "🌆", town: "Taguig"),
];

const Map<String, List<String>> DEST_SPOT_KEYWORDS = {
  "Taal Heritage Town": ["Taal", "Marian Orchard", "barako", "pilgrim", "Basilica"],
  "Lipa Heritage District": ["Lipa", "barako", "lomi", "Don Juan", "Outlets", "S&R", "SM City Lipa", "The Old Grove", "Bulacnin", "Summit"],
  "Nasugbu": ["Nasugbu", "beach", "coast"],
  "Baguio City": ["Baguio", "Cathedral", "Camp John Hay", "Café by the Ruins", "SM City Baguio", "Burnham", "pine"],
  "Tagaytay": ["Tagaytay", "People", "People's", "Twin Lakes", "Taal Lake", "vineyard", "volcano"],
  "Vigan": ["Vigan", "Crisologo", "Ilocos", "heritage"],
  "La Union": ["La Union", "San Juan", "Surf", "Flotsam"],
  "Bicol": ["Bicol", "Cagsawa", "Mayon", "Albay", "volcano"],
  "Manila": ["Manila", "Intramuros", "Fort Santiago"],
  "Taguig": ["Taguig", "BGC", "Bonifacio", "High Street"],
};

String peso(int n) => "₱$n";

double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
  const r = 6371.0;
  final dLat = (lat2 - lat1) * (math.pi / 180.0);
  final dLon = (lon2 - lon1) * (math.pi / 180.0);
  final a = (math.sin(dLat / 2) * math.sin(dLat / 2)) +
      math.cos(lat1 * (math.pi / 180.0)) *
          math.cos(lat2 * (math.pi / 180.0)) *
          (math.sin(dLon / 2) * math.sin(dLon / 2));
  final c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a));
  return ((r * c) * 10).round() / 10.0;
}
