import 'package:flutter/material.dart';

class RouteScreen extends StatelessWidget {
  final String spotKeys;
  final String tripLabel;

  const RouteScreen({
    super.key,
    required this.spotKeys,
    required this.tripLabel,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(tripLabel)),
      body: const Center(
        child: Text("Route Navigation Screen"),
      ),
    );
  }
}
