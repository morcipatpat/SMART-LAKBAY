import 'package:flutter/material.dart';
import 'destination_detail_widget.dart';

class TripCustomizerScreen extends StatelessWidget {
  final String destination;
  final int budget;

  const TripCustomizerScreen({
    super.key,
    required this.destination,
    required this.budget,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Trip Customizer")),
      body: Center(
        child: Text("Customizing trip for $destination with budget ₱$budget"),
      ),
    );
  }
}
