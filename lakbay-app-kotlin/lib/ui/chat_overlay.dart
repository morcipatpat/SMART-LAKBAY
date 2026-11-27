import 'package:flutter/material.dart';
import 'chat_screen.dart';
import '../engine/lakbay_brain.dart';
import '../models/models.dart';

class ChatOverlay extends StatefulWidget {
  final VoidCallback onClose;
  final String destination;
  final int budget;
  final (double, double) userLocation;

  const ChatOverlay({
    super.key,
    required this.onClose,
    this.destination = "Taal Heritage Town",
    this.budget = 3500,
    this.userLocation = const (13.9550, 121.1600),
  });

  @override
  State<ChatOverlay> createState() => _ChatOverlayState();
}

class _ChatOverlayState extends State<ChatOverlay> {
  final List<ChatMessage> messages = [
    ChatMessage(
      role: "bot",
      text: "Kamusta! I'm Lakbay AI 🌋 — your Batangas travel assistant. Ask me to plan a trip, find the best food/cafés, check prices, or how far a place is. Tap a suggestion below to start!",
    ),
  ];
  final TextEditingController inputController = TextEditingController();

  final List<String> chips = [
    "Plan my Taal trip",
    "Best lomi nearby?",
    "Budget ₱2000",
  ];

  void send(String text) {
    if (text.trim().isEmpty) return;
    final ctx = BrainContext(
      destination: widget.destination,
      budget: widget.budget,
      userLocation: widget.userLocation,
      spots: SPOTS,
    );
    final reply = LakbayBrain.answer(text, ctx);
    setState(() {
      messages.add(ChatMessage(role: "user", text: text));
      messages.add(ChatMessage(role: "bot", text: reply));
      inputController.clear();
    });
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: widget.onClose,
      child: Container(
        color: Colors.black.withValues(alpha: 0.45),
        alignment: Alignment.bottomCenter,
        child: GestureDetector(
          onTap: () {}, // prevent tap through
          child: Container(
            height: MediaQuery.of(context).size.height * 0.6,
            decoration: const BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.only(
                topLeft: Radius.circular(24),
                topRight: Radius.circular(24),
              ),
            ),
            child: Column(
              children: [
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  child: Row(
                    children: [
                      const Text("🌋", style: TextStyle(fontSize: 18)),
                      const SizedBox(width: 8),
                      const Text(
                        "Lakbay AI",
                        style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Color(0xFF0F1B3D)),
                      ),
                      const Spacer(),
                      IconButton(
                        icon: const Icon(Icons.close, color: Color(0xFF64708A)),
                        onPressed: widget.onClose,
                      ),
                    ],
                  ),
                ),
                const Divider(height: 1),
                Expanded(
                  child: ListView.builder(
                    padding: const EdgeInsets.all(12),
                    itemCount: messages.length,
                    itemBuilder: (context, index) {
                      final msg = messages[index];
                      final isUser = msg.role == "user";
                      return Align(
                        alignment: isUser ? Alignment.centerRight : Alignment.centerLeft,
                        child: Container(
                          margin: const EdgeInsets.symmetric(vertical: 4),
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: isUser ? const Color(0xFF0A4FD6) : const Color(0xFFF1F5FB),
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Text(
                            msg.text,
                            style: TextStyle(
                              color: isUser ? Colors.white : const Color(0xFF1E293B),
                              fontSize: 14,
                            ),
                          ),
                        ),
                      );
                    },
                  ),
                ),
                SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                  child: Row(
                    children: chips.map((chip) {
                      return Padding(
                        padding: const EdgeInsets.only(right: 6),
                        child: ActionChip(
                          label: Text(chip, style: const TextStyle(fontSize: 11)),
                          onPressed: () => send(chip),
                        ),
                      );
                    }).toList(),
                  ),
                ),
                Container(
                  color: const Color(0xFFF8FAFC),
                  padding: const EdgeInsets.all(8),
                  child: Row(
                    children: [
                      Expanded(
                        child: TextField(
                          controller: inputController,
                          decoration: InputDecoration(
                            hintText: "Ask about places, budget, plans…",
                            filled: true,
                            fillColor: Colors.white,
                            isDense: true,
                            border: OutlineInputBorder(borderRadius: BorderRadius.circular(24), borderSide: BorderSide.none),
                          ),
                        ),
                      ),
                      const SizedBox(width: 8),
                      ElevatedButton(
                        onPressed: () => send(inputController.text),
                        style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF0A4FD6)),
                        child: const Text("Send", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class ChatMessage {
  final String role;
  final String text;
  ChatMessage({required this.role, required this.text});
}
