import 'package:flutter/material.dart';
import 'chat_overlay.dart';

class ChatScreen extends StatelessWidget {
  final VoidCallback onClose;
  final String destination;
  final int budget;
  final (double, double) userLocation;

  const ChatScreen({
    super.key,
    required this.onClose,
    this.destination = "Taal Heritage Town",
    this.budget = 3500,
    this.userLocation = const (13.9550, 121.1600),
  });

  @override
  Widget build(BuildContext context) {
    return ChatOverlay(
      onClose: onClose,
      destination: destination,
      budget: budget,
      userLocation: userLocation,
    );
  }
}
