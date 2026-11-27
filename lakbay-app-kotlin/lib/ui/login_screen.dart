import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/models.dart';
import '../services/user_database_manager.dart';

class LoginCard extends StatefulWidget {
  final ValueChanged<String>? onSuccess;
  final VoidCallback? onGuest;
  final bool compact;

  const LoginCard({
    super.key,
    this.onSuccess,
    this.onGuest,
    this.compact = false,
  });

  @override
  State<LoginCard> createState() => _LoginCardState();
}

class _LoginCardState extends State<LoginCard> {
  static const Color brandBlue = Color(0xFF0A4FD6);
  static const Color brandGreen = Color(0xFF21C55E);
  static const Color grayText = Color(0xFF64708A);
  static const Color fieldBorder = Color(0xFFC7D0E0);
  static const Color darkText = Color(0xFF0F1B3D);

  int mode = 0; // 0 = sign in, 1 = register
  final TextEditingController emailController =
      TextEditingController(text: "morcillaj14@gmail.com");
  final TextEditingController passwordController = TextEditingController();
  final TextEditingController confirmPasswordController =
      TextEditingController();
  bool showPassword = false;
  String error = "";

  @override
  void dispose() {
    emailController.dispose();
    passwordController.dispose();
    confirmPasswordController.dispose();
    super.dispose();
  }

  Future<void> submit() async {
    setState(() => error = "");
    final e = emailController.text.trim();
    final p = passwordController.text;
    final cp = confirmPasswordController.text;

    if (!e.contains("@") || !e.contains(".")) {
      setState(() => error = "Please enter a valid email address");
      return;
    }
    if (p.length < 6) {
      setState(() => error = "Password must be at least 6 characters");
      return;
    }

    final prefs = await SharedPreferences.getInstance();
    final defaultName =
        e.split("@")[0].substring(0, 1).toUpperCase() + e.split("@")[0].substring(1);

    await UserDatabaseManager.restoreUserSessionFromFolder(e);
    final savedName = prefs.getString("smart_lakbay_session_name");
    if (savedName == null || savedName.isEmpty) {
      await prefs.setString("smart_lakbay_session_email", e);
      await prefs.setString("smart_lakbay_session_name", defaultName);
      await prefs.remove("smart_lakbay_session_is_guest");
      await UserDatabaseManager.saveUserData(
        email: e,
        name: defaultName,
        phone: "+63 912 345 6789",
        avatar: "👤",
        lang: "en",
        notifs: true,
      );
    } else {
      await prefs.setString("smart_lakbay_session_email", e);
      await prefs.remove("smart_lakbay_session_is_guest");
    }

    if (mode == 0) {
      final acc = await DemoAccounts.find(e);
      if (acc == null) {
        setState(() => error = "No account found with that email. Switch to Register.");
      } else if (acc.value != p) {
        setState(() => error = "Incorrect password. Try again.");
      } else {
        if (widget.onSuccess != null) {
          widget.onSuccess!(e);
        } else {
          context.go('/main');
        }
      }
    } else {
      if (p != cp) {
        setState(() => error = "Passwords do not match. Please retype.");
        return;
      }
      final acc = await DemoAccounts.find(e);
      if (acc != null) {
        setState(() => error = "Account already exists. Please sign in.");
      } else {
        await DemoAccounts.add(e, p);
        if (widget.onSuccess != null) {
          widget.onSuccess!(e);
        } else {
          context.go('/main');
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final content = Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        const Row(
          children: [
            Text("🌋", style: TextStyle(fontSize: 22)),
            SizedBox(width: 8),
            Text(
              "Smart Lakbay",
              style: TextStyle(
                color: brandBlue,
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
        const Padding(
          padding: EdgeInsets.only(top: 4, bottom: 12),
          child: Text(
            "Sign in or register your account with Firebase",
            textAlign: TextAlign.center,
            style: TextStyle(color: grayText, fontSize: 12),
          ),
        ),
        // Sign In / Register segmented tabs
        Container(
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(10),
            border: Border.all(color: fieldBorder),
          ),
          padding: const EdgeInsets.all(4),
          child: Row(
            children: [
              Expanded(
                child: GestureDetector(
                  onTap: () => setState(() {
                    mode = 0;
                    error = "";
                  }),
                  child: Container(
                    padding: const EdgeInsets.symmetric(vertical: 8),
                    decoration: BoxDecoration(
                      color: mode == 0 ? const Color(0xFFEAF1FF) : Colors.transparent,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    alignment: Alignment.center,
                    child: Text(
                      "Sign In",
                      style: TextStyle(
                        color: mode == 0 ? brandBlue : grayText,
                        fontWeight: mode == 0 ? FontWeight.bold : FontWeight.normal,
                        fontSize: 14,
                      ),
                    ),
                  ),
                ),
              ),
              Expanded(
                child: GestureDetector(
                  onTap: () => setState(() {
                    mode = 1;
                    error = "";
                  }),
                  child: Container(
                    padding: const EdgeInsets.symmetric(vertical: 8),
                    decoration: BoxDecoration(
                      color: mode == 1 ? const Color(0xFFEAF1FF) : Colors.transparent,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    alignment: Alignment.center,
                    child: Text(
                      "Register",
                      style: TextStyle(
                        color: mode == 1 ? brandBlue : grayText,
                        fontWeight: mode == 1 ? FontWeight.bold : FontWeight.normal,
                        fontSize: 14,
                      ),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: 12),
        // Email field
        TextField(
          controller: emailController,
          style: const TextStyle(color: brandBlue, fontSize: 15),
          decoration: InputDecoration(
            hintText: "Email address",
            hintStyle: const TextStyle(color: grayText, fontSize: 14),
            contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
            filled: true,
            fillColor: Colors.white,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderBorder(color: fieldBorder),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: fieldBorder),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: brandBlue),
            ),
          ),
        ),
        const SizedBox(height: 10),
        // Password field + eye toggle
        Row(
          children: [
            Expanded(
              child: TextField(
                controller: passwordController,
                obscureText: !showPassword,
                style: const TextStyle(color: darkText, fontSize: 15),
                decoration: InputDecoration(
                  hintText: "Password",
                  hintStyle: const TextStyle(color: grayText, fontSize: 14),
                  contentPadding:
                      const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
                  filled: true,
                  fillColor: Colors.white,
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: const BorderSide(color: fieldBorder),
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: const BorderSide(color: fieldBorder),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: const BorderSide(color: brandBlue),
                  ),
                ),
              ),
            ),
            const SizedBox(width: 8),
            GestureDetector(
              onTap: () => setState(() => showPassword = !showPassword),
              child: Container(
                width: 44,
                height: 44,
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: fieldBorder),
                ),
                alignment: Alignment.center,
                child: Text(showPassword ? "🙈" : "👁️", style: const TextStyle(fontSize: 16)),
              ),
            ),
          ],
        ),
        // Retype Password field (mode == 1)
        if (mode == 1) ...[
          const SizedBox(height: 10),
          TextField(
            controller: confirmPasswordController,
            obscureText: !showPassword,
            style: const TextStyle(color: darkText, fontSize: 15),
            decoration: InputDecoration(
              hintText: "Retype password",
              hintStyle: const TextStyle(color: grayText, fontSize: 14),
              contentPadding:
                  const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
              filled: true,
              fillColor: Colors.white,
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8),
                borderSide: const BorderSide(color: fieldBorder),
              ),
              enabledBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8),
                borderSide: const BorderSide(color: fieldBorder),
              ),
              focusedBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8),
                borderSide: const BorderSide(color: brandBlue),
              ),
            ),
          ),
        ],
        if (mode == 0) ...[
          const SizedBox(height: 6),
          const Text(
            "Forgot password?",
            style: TextStyle(color: grayText, fontSize: 12),
          ),
        ],
        if (error.isNotEmpty) ...[
          Padding(
            padding: const EdgeInsets.only(top: 8),
            child: Text(
              error,
              style: const TextStyle(color: Color(0xFFE5484D), fontSize: 12),
            ),
          ),
        ],
        const SizedBox(height: 14),
        SizedBox(
          height: 46,
          child: ElevatedButton(
            onPressed: submit,
            style: ElevatedButton.styleFrom(
              backgroundColor: brandGreen,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
            ),
            child: Text(
              mode == 0 ? "Sign In" : "Create Account",
              style: const TextStyle(
                color: Colors.white,
                fontWeight: FontWeight.bold,
                fontSize: 15,
              ),
            ),
          ),
        ),
        const SizedBox(height: 10),
        GestureDetector(
          onTap: () => setState(() {
            mode = 1 - mode;
            error = "";
          }),
          child: Text(
            mode == 0 ? "New user? Create account" : "Already have an account? Sign In",
            textAlign: TextAlign.center,
            style: const TextStyle(color: grayText, fontSize: 12),
          ),
        ),
        if (widget.onGuest != null) ...[
          const SizedBox(height: 8),
          GestureDetector(
            onTap: widget.onGuest,
            child: const Text(
              "Explore as Guest",
              textAlign: TextAlign.center,
              style: TextStyle(
                color: brandBlue,
                fontSize: 12,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ],
    );

    if (widget.compact) {
      return content;
    }

    return Scaffold(
      backgroundColor: const Color(0xFFF7F9FC),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: content,
        ),
      ),
    );
  }
}
