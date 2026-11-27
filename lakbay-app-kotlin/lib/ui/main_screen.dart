import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/app_language.dart';
import 'explore_tab.dart';
import 'trips_screen.dart';
import 'reviews_screen.dart';
import 'profile_screen.dart';
import 'chat_screen.dart';
import 'auth_gate_overlay.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int selectedTab = 0;
  bool showChatBot = false;
  bool showNotificationsDialog = false;
  String selectedDestinationName = "Taal Basilica";

  int authVersion = 0;
  int langVersion = 0;

  final List<String> tabKeys = ["explore", "map", "trips", "reviews", "profile"];
  final List<IconData> icons = [
    Icons.explore,
    Icons.map,
    Icons.favorite,
    Icons.star,
    Icons.person,
  ];

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<SharedPreferences>(
      future: SharedPreferences.getInstance(),
      builder: (context, snapshot) {
        if (!snapshot.hasData) {
          return const Scaffold(body: Center(child: CircularProgressIndicator()));
        }
        final prefs = snapshot.data!;
        final savedEmail = prefs.getString("smart_lakbay_session_email");
        final isGuest = prefs.getBool("smart_lakbay_session_is_guest") ?? false;
        final isLoggedIn = (savedEmail != null && savedEmail.isNotEmpty) || isGuest;

        return Scaffold(
          body: Stack(
            children: [
              Scaffold(
                bottomNavigationBar: NavigationBar(
                  selectedIndex: selectedTab,
                  onDestinationSelected: (index) {
                    if (tabKeys[index] == "map") {
                      context.go('/map');
                    } else {
                      setState(() => selectedTab = index);
                    }
                  },
                  destinations: List.generate(tabKeys.length, (index) {
                    final key = tabKeys[index];
                    final label = AppLanguage.t("en", key); // fallback / dynamic
                    return NavigationDestination(
                      icon: Icon(icons[index]),
                      label: label,
                    );
                  }),
                ),
                floatingActionButton: FloatingActionButton(
                  onPressed: () => setState(() => showChatBot = true),
                  backgroundColor: const Color(0xFF22C55E),
                  child: const Icon(Icons.chat, color: Colors.white),
                ),
                body: SafeArea(
                  child: IndexedStack(
                    index: selectedTab,
                    children: [
                      ExploreTab(
                        selectedDestinationName: selectedDestinationName,
                        isLoggedIn: isLoggedIn,
                        onSelectDestination: (name) => setState(() => selectedDestinationName = name),
                        onOpenNotifications: () => setState(() => showNotificationsDialog = true),
                      ),
                      const SizedBox.shrink(), // map is a separate route
                      const TripsScreen(),
                      const ReviewsScreen(),
                      ProfileScreen(
                        onLanguageChanged: () => setState(() => langVersion++),
                      ),
                    ],
                  ),
                ),
              ),

              // Chat overlay
              if (showChatBot)
                ChatScreen(onClose: () => setState(() => showChatBot = false)),

              // Notifications Dialog
              if (showNotificationsDialog)
                AlertDialog(
                  title: const Row(
                    children: [
                      Text("🔔", style: TextStyle(fontSize: 20)),
                      SizedBox(width: 8),
                      Text("Notifications & Announcements", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                    ],
                  ),
                  content: SizedBox(
                    width: double.maxFinite,
                    height: 340,
                    child: ListView(
                      children: const [
                        ListTile(
                          leading: Text("🌟", style: TextStyle(fontSize: 24)),
                          title: Text("Welcome Back, Traveler! 🌋", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Color(0xFF0A4FD6))),
                          subtitle: Text("Welcome back! Are you ready for another unforgettable Batangas trip today?", style: TextStyle(fontSize: 12)),
                        ),
                        ListTile(
                          leading: Text("🎒", style: TextStyle(fontSize: 24)),
                          title: Text("Upcoming Trip Reminder 📅", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Color(0xFF0A4FD6))),
                          subtitle: Text("Your 'Taal Heritage Trip' is scheduled for Nov 8, 2026.", style: TextStyle(fontSize: 12)),
                        ),
                      ],
                    ),
                  ),
                  actions: [
                    TextButton(
                      onPressed: () => setState(() => showNotificationsDialog = false),
                      child: const Text("Close"),
                    ),
                  ],
                ),

              // Auth Gate Overlay if unauthenticated
              if (!isLoggedIn)
                AuthGateOverlay(
                  onUnlocked: (email) {
                    prefs.setString("smart_lakbay_session_email", email);
                    prefs.remove("smart_lakbay_session_is_guest");
                    setState(() => authVersion++);
                  },
                  onGuest: () {
                    prefs.setBool("smart_lakbay_session_is_guest", true);
                    prefs.setString("smart_lakbay_session_email", "guest@lakbay.app");
                    setState(() => authVersion++);
                  },
                ),
            ],
          ),
        );
      },
    );
  }
}
