import 'package:shared_preferences/shared_preferences.dart';

class UserDatabaseManager {
  static const String _sessionPrefs = "smart_lakbay_session";

  static String sanitizeEmail(String email) {
    return email.toLowerCase().replaceAll(RegExp(r'[^a-z0-9]'), '_');
  }

  static Future<void> saveUserData({
    required String email,
    required String name,
    required String phone,
    required String avatar,
    required String lang,
    required bool notifs,
  }) async {
    if (email.trim().isEmpty) return;
    final prefs = await SharedPreferences.getInstance();
    final sanitized = sanitizeEmail(email);
    await prefs.setString("user_${sanitized}_name", name);
    await prefs.setString("user_${sanitized}_email", email);
    await prefs.setString("user_${sanitized}_phone", phone);
    await prefs.setString("user_${sanitized}_avatar", avatar);
    await prefs.setString("user_${sanitized}_lang", lang);
    await prefs.setBool("user_${sanitized}_notifs", notifs);
  }

  static Future<Map<String, String>> loadUserData(String email) async {
    if (email.trim().isEmpty) return {};
    final prefs = await SharedPreferences.getInstance();
    final sanitized = sanitizeEmail(email);

    final name = prefs.getString("user_${sanitized}_name");
    if (name == null) return {};

    return {
      "name": name,
      "email": prefs.getString("user_${sanitized}_email") ?? email,
      "phone": prefs.getString("user_${sanitized}_phone") ?? "+63 912 345 6789",
      "avatar": prefs.getString("user_${sanitized}_avatar") ?? "👤",
      "lang": prefs.getString("user_${sanitized}_lang") ?? "en",
      "notifs": (prefs.getBool("user_${sanitized}_notifs") ?? true).toString(),
    };
  }

  static Future<String> getCurrentUserEmail() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString("${_sessionPrefs}_email") ?? "";
  }

  static Future<void> syncCurrentSessionToUserFolder() async {
    final prefs = await SharedPreferences.getInstance();
    final email = prefs.getString("${_sessionPrefs}_email") ?? "";
    if (email.isEmpty || email == "guest@lakbay.app") return;

    final name = prefs.getString("${_sessionPrefs}_name") ?? "Traveler";
    final phone = prefs.getString("${_sessionPrefs}_phone") ?? "+63 912 345 6789";
    final avatar = prefs.getString("${_sessionPrefs}_profile_avatar") ?? "👤";
    final lang = prefs.getString("${_sessionPrefs}_lang") ?? "en";
    final notifs = prefs.getBool("${_sessionPrefs}_notifications") ?? true;

    await saveUserData(
      email: email,
      name: name,
      phone: phone,
      avatar: avatar,
      lang: lang,
      notifs: notifs,
    );
  }

  static Future<void> restoreUserSessionFromFolder(String email) async {
    if (email.isEmpty || email == "guest@lakbay.app") return;
    final data = await loadUserData(email);
    if (data.isNotEmpty) {
      final prefs = await SharedPreferences.getInstance();
      if (data["name"] != null) await prefs.setString("${_sessionPrefs}_name", data["name"]!);
      if (data["phone"] != null) await prefs.setString("${_sessionPrefs}_phone", data["phone"]!);
      if (data["avatar"] != null) await prefs.setString("${_sessionPrefs}_profile_avatar", data["avatar"]!);
      if (data["lang"] != null) await prefs.setString("${_sessionPrefs}_lang", data["lang"]!);
      if (data["notifs"] != null) {
        await prefs.setBool("${_sessionPrefs}_notifications", data["notifs"] == "true");
      }
    }
  }
}
