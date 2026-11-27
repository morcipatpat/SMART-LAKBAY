import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/app_language.dart';
import '../models/community_store.dart';
import '../models/models.dart';

class ReviewsScreen extends StatefulWidget {
  const ReviewsScreen({super.key});

  @override
  State<ReviewsScreen> createState() => _ReviewsScreenState();
}

class _ReviewsScreenState extends State<ReviewsScreen> {
  List<CommunityReview> reviews = [];
  bool isLoading = true;
  String userName = "John Ray";
  String reviewText = "";
  String selectedTrip = "Taal Heritage Trip";
  int selectedRating = 5;
  Map<String, String> commentInputs = {};

  @override
  void initState() {
    super.initState();
    _loadReviews();
  }

  Future<void> _loadReviews() async {
    final prefs = await SharedPreferences.getInstance();
    final name = prefs.getString("smart_lakbay_session_name") ?? "John Ray";
    final list = await CommunityStore.all();
    setState(() {
      userName = name;
      reviews = list;
      isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    return Scaffold(
      backgroundColor: Colors.white,
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          const Text(
            "Traveler Reviews & Community",
            style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: Color(0xFF0F1B3D)),
          ),
          const SizedBox(height: 4),
          const Text(
            "Share your itinerary experiences and connect with fellow travelers",
            style: TextStyle(color: Color(0xFF64708A), fontSize: 13),
          ),
          const SizedBox(height: 16),
          // Post Review Card
          Card(
            color: const Color(0xFFEFF6FF),
            elevation: 2,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text("✍️ Post Itinerary Review", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Color(0xFF0A4FD6))),
                  const SizedBox(height: 12),
                  const Text("Select Trip / Destination:", style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Color(0xFF64708A))),
                  const SizedBox(height: 6),
                  Row(
                    children: ["Taal Heritage Trip", "Nasugbu Beach Weekend"].map((trip) {
                      final isSelected = selectedTrip == trip;
                      return Padding(
                        padding: const EdgeInsets.only(right: 8),
                        child: GestureDetector(
                          onTap: () => setState(() => selectedTrip = trip),
                          child: Container(
                            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                            decoration: BoxDecoration(
                              color: isSelected ? const Color(0xFF0A4FD6) : Colors.white,
                              borderRadius: BorderRadius.circular(50),
                            ),
                            child: Text(
                              trip,
                              style: TextStyle(color: isSelected ? Colors.white : const Color(0xFF0A4FD6), fontWeight: FontWeight.bold, fontSize: 12),
                            ),
                          ),
                        ),
                      );
                    }).toList(),
                  ),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      const Text("Rating: ", style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Color(0xFF64708A))),
                      ...List.generate(5, (index) {
                        final star = index + 1;
                        return IconButton(
                          icon: Icon(Icons.star, color: star <= selectedRating ? Colors.amber : Colors.grey.shade300),
                          onPressed: () => setState(() => selectedRating = star),
                        );
                      }),
                    ],
                  ),
                  TextField(
                    onChanged: (v) => reviewText = v,
                    decoration: InputDecoration(
                      hintText: "Share your itinerary experience...",
                      filled: true,
                      fillColor: Colors.white,
                      border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                    ),
                    maxLines: 3,
                  ),
                  const SizedBox(height: 12),
                  Align(
                    alignment: Alignment.centerRight,
                    child: ElevatedButton(
                      onPressed: () async {
                        if (reviewText.trim().isNotEmpty) {
                          final newRev = CommunityReview(
                            id: DateTime.now().millisecondsSinceEpoch.toString(),
                            author: userName,
                            tripName: selectedTrip,
                            text: reviewText,
                            date: "Nov 14, 2026",
                            rating: selectedRating,
                          );
                          await CommunityStore.addReview(newRev);
                          reviewText = "";
                          _loadReviews();
                        }
                      },
                      style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF21C55E)),
                      child: const Text("Post Review", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 20),
          const Text("Recent Community Reviews", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 12),
          ...reviews.map((r) {
            return Card(
              color: const Color(0xFFF8FAFC),
              elevation: 1,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              margin: const EdgeInsets.symmetric(vertical: 8),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.between,
                      children: [
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(r.author, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                            Text(r.tripName, style: const TextStyle(color: Color(0xFF0A4FD6), fontSize: 12, fontWeight: FontWeight.bold)),
                          ],
                        ),
                        Text(r.date, style: const TextStyle(color: Color(0xFF64708A), fontSize: 11)),
                      ],
                    ),
                    const SizedBox(height: 6),
                    Row(
                      children: List.generate(5, (index) => Icon(Icons.star, size: 16, color: index < r.rating ? Colors.amber : Colors.grey.shade300)),
                    ),
                    const SizedBox(height: 8),
                    Text(r.text, style: const TextStyle(fontSize: 14)),
                    if (r.comments.isNotEmpty) ...[
                      const Divider(height: 20),
                      const Text("Comments", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12, color: Color(0xFF0A4FD6))),
                      ...r.comments.map((c) => Padding(
                            padding: const EdgeInsets.symmetric(vertical: 2),
                            child: Text("${c.author}: ${c.text}", style: const TextStyle(fontSize: 12)),
                          )),
                    ],
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Expanded(
                          child: TextField(
                            onChanged: (v) => commentInputs[r.id] = v,
                            decoration: const InputDecoration(
                              hintText: "Add a comment...",
                              isDense: true,
                              border: OutlineInputBorder(),
                            ),
                          ),
                        ),
                        IconButton(
                          icon: const Icon(Icons.send, color: Color(0xFF0A4FD6)),
                          onPressed: () async {
                            final commentText = commentInputs[r.id];
                            if (commentText != null && commentText.trim().isNotEmpty) {
                              await CommunityStore.addComment(
                                r.id,
                                CommunityComment(id: DateTime.now().millisecondsSinceEpoch.toString(), author: userName, text: commentText),
                              );
                              commentInputs[r.id] = "";
                              _loadReviews();
                            }
                          },
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            );
          }),
        ],
      ),
    );
  }
}
