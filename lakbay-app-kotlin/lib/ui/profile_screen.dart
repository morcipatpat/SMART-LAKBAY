import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:go_router/go_router.dart';
import '../models/app_language.dart';
import '../models/user_database_manager.dart';

class ProfileScreen extends StatefulWidget {
  final VoidCallback onLanguageChanged;

  const ProfileScreen({super.key, required this.onLanguageChanged});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  bool isGuest = false;
  String name = "MOJACKO";
  String email = "morcillaj14@gmail.com";
  String phone = "+63 912 345 6789";
  bool notifsOn = true;
  String selectedLang = "en";
  String profileAvatar = "👤";

  @override
  void initState() {
    super.initState();
    _loadProfile();
  }

  Future<void> _loadProfile() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      isGuest = prefs.getBool("smart_lakbay_session_is_guest") ?? false;
      name = prefs.getString("smart_lakbay_session_name") ?? "MOJACKO";
      email = prefs.getString("smart_lakbay_session_email") ?? "morcillaj14@gmail.com";
      phone = prefs.getString("smart_lakbay_session_phone") ?? "+63 912 345 6789";
      notifsOn = prefs.getBool("smart_lakbay_session_notifications") ?? true;
      selectedLang = prefs.getString("smart_lakbay_session_lang") ?? "en";
      profileAvatar = prefs.getString("smart_lakbay_session_profile_avatar") ?? "👤";
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF1F5F9),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          const Center(
            child: Text(
              "Profile & Settings",
              style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: Color(0xFF0F1B3D)),
            ),
          ),
          const SizedBox(height: 16),
          Card(
            color: Colors.white,
            elevation: 2,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            child: Padding(
              padding: const EdgeInsets.all(24),
              child: Column(
                children: [
                  Container(
                    width: 80,
                    height: 80,
                    decoration: const BoxDecoration(
                      color: Color(0xFFEAF1FF),
                      shape: BoxShape.circle,
                    ),
                    alignment: Alignment.center,
                    child: Text(profileAvatar, style: const TextStyle(fontSize: 36)),
                  ),
                  const SizedBox(height: 12),
                  Text(name, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF0F1B3D))),
                  Text(email, style: const TextStyle(color: Color(0xFF64708A), fontSize: 13)),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          Card(
            color: Colors.white,
            elevation: 2,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            child: Column(
              children: [
                SwitchListTile(
                  title: const Text("Notifications", style: TextStyle(fontWeight: FontWeight.medium)),
                  value: notifsOn,
                  onChanged: (val) async {
                    setState(() => notifsOn = val);
                    final prefs = await SharedPreferences.getInstance();
                    await prefs.setBool("smart_lakbay_session_notifications", val);
                    await UserDatabaseManager.syncCurrentSessionToUserFolder();
                  },
                ),
              ],
            ),
          ),
          const SizedBox(height: 20),
          ElevatedButton.icon(
            onPressed: () async {
              await UserDatabaseManager.syncCurrentSessionToUserFolder();
              final prefs = await SharedPreferences.getInstance();
              await prefs.clear();
              if (mounted) context.go('/login');
            },
            icon: const Icon(Icons.logout, color: Colors.white),
            label: const Text("Sign Out", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
          ),
        ],
      ),
    );
  }
}
