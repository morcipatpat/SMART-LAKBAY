import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'destination_detail_widget.dart';

class ExploreTab extends StatefulWidget {
  final String selectedDestinationName;
  final bool isLoggedIn;
  final ValueChanged<String> onSelectDestination;
  final VoidCallback onOpenNotifications;

  const ExploreTab({
    super.key,
    required this.selectedDestinationName,
    required this.isLoggedIn,
    required this.onSelectDestination,
    required this.onOpenNotifications,
  });

  @override
  State<ExploreTab> createState() => _ExploreTabState();
}

class _ExploreTabState extends State<ExploreTab> {
  String searchQuery = "";

  @override
  Widget build(BuildContext context) {
    final filteredDestinations = searchQuery.isEmpty
        ? DESTINATIONS
        : DESTINATIONS.where((d) =>
            d.name.toLowerCase().contains(searchQuery.toLowerCase()) ||
            d.town.toLowerCase().contains(searchQuery.toLowerCase())).toList();

    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        // Header
        Row(
          children: [
            const Text("🌋 ", style: TextStyle(fontSize: 20)),
            const Text("Smart ", style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Color(0xFF0A4FD6))),
            const Text("Lakbay", style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Color(0xFF22C55E))),
            const Spacer(),
            // Bell
            GestureDetector(
              onTap: widget.onOpenNotifications,
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                decoration: BoxDecoration(
                  color: const Color(0xFFEAF1FF),
                  borderRadius: BorderRadius.circular(50),
                ),
                child: const Text("🔔", style: TextStyle(fontSize: 14)),
              ),
            ),
            const SizedBox(width: 8),
            // Profile / Login pill
            GestureDetector(
              onTap: () {
                if (!widget.isLoggedIn) {
                  context.go('/login');
                }
              },
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                decoration: BoxDecoration(
                  color: widget.isLoggedIn ? const Color(0xFF0A4FD6) : const Color(0xFF22C55E),
                  borderRadius: BorderRadius.circular(50),
                ),
                child: Row(
                  children: [
                    Text(widget.isLoggedIn ? "👤 " : "🔑 ", style: const TextStyle(fontSize: 12)),
                    Text(
                      widget.isLoggedIn ? "Traveler" : "Sign In",
                      style: const TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),
        // Greeting Box
        Card(
          color: const Color(0xFFEAF1FF),
          elevation: 0,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          child: const Padding(
            padding: EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  "Welcome back, Traveler! 🌋",
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Color(0xFF0A4FD6)),
                ),
                SizedBox(height: 4),
                Text(
                  "Are you ready for another unforgettable trip today? Discover Batangas heritage, beaches & mountain trails.",
                  style: TextStyle(fontSize: 14, color: Color(0xFF1E293B)),
                ),
              ],
            ),
          ),
        ),
        const SizedBox(height: 16),
        // Search Bar
        TextField(
          onChanged: (v) => setState(() => searchQuery = v),
          decoration: InputDecoration(
            hintText: "Search popular Batangas trips...",
            prefixIcon: const Icon(Icons.search),
            filled: true,
            fillColor: Colors.white,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(24),
              borderSide: BorderSide.none,
            ),
          ),
        ),
        const SizedBox(height: 16),
        const Text(
          "Popular destinations",
          style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 8),
        // Destinations list box
        Card(
          elevation: 2,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          child: SizedBox(
            height: 230,
            child: ListView.builder(
              padding: const EdgeInsets.all(8),
              itemCount: filteredDestinations.length,
              itemBuilder: (context, index) {
                final d = filteredDestinations[index];
                final isSelected = d.name == widget.selectedDestinationName;
                return Card(
                  elevation: isSelected ? 4 : 1,
                  color: isSelected ? const Color(0xFFEAF1FF) : null,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  child: ListTile(
                    onTap: () => widget.onSelectDestination(d.name),
                    leading: Text(d.icon, style: const TextStyle(fontSize: 26)),
                    title: Text(d.name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                    subtitle: Text(d.dist, style: const TextStyle(fontSize: 12)),
                  ),
                );
              },
            ),
          ),
        ),
        const SizedBox(height: 16),
        DestinationDetailWidget(
          destinationName: widget.selectedDestinationName,
          onSearchTrips: (dest, budget) {
            context.go('/customizer/$dest/$budget');
          },
        ),
      ],
    );
  }
}
