import 'package:flutter/material.dart';
import 'login_screen.dart';

class AuthGateOverlay extends StatelessWidget {
  final ValueChanged<String> onUnlocked;
  final VoidCallback onGuest;

  const AuthGateOverlay({
    super.key,
    required this.onUnlocked,
    required this.onGuest,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.black.withValues(alpha: 0.55),
      child: Center(
        child: Container(
          constraints: const BoxConstraints(maxWidth: 340),
          margin: const EdgeInsets.all(24),
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(20),
            boxShadow: const [
              BoxShadow(
                color: Colors.black26,
                blurRadius: 10,
                offset: Offset(0, 4),
              )
            ],
          ),
          child: LoginCard(
            onSuccess: onUnlocked,
            onGuest: onGuest,
            compact: true,
          ),
        ),
      ),
    );
  }
}
