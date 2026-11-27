import 'package:flutter/material.dart';

class ItineraryScreen extends StatelessWidget {
  final String destination;
  final int budget;
  final String spotKeys;

  const ItineraryScreen({
    super.key,
    required this.destination,
    required this.budget,
    required this.spotKeys,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Itinerary")),
      body: Center(
        child: Text("Itinerary for $destination (Budget: ₱$budget)"),
      ),
    );
  }
}
