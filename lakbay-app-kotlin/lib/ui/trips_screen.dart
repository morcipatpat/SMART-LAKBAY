import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../models/models.dart';
import '../services/trip_store.dart';

class TripsScreen extends StatefulWidget {
  const TripsScreen({super.key});

  @override
  State<TripsScreen> createState() => _TripsScreenState();
}

class _TripsScreenState extends State<TripsScreen> {
  int selectedTab = 0; // 0 = Upcoming, 1 = History
  List<SavedTrip> trips = [];
  bool isLoading = true;
  Set<String> expandedTripIds = {};

  @override
  void initState() {
    super.initState();
    _loadTrips();
  }

  Future<void> _loadTrips() async {
    final list = await TripStore.all();
    setState(() {
      trips = list;
      isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    final filtered = trips.where((t) => selectedTab == 0 ? !t.done : t.done).toList();

    return Scaffold(
      backgroundColor: const Color(0xFFF7F9FC),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            // Tabs
            Row(
              children: [
                Container(
                  decoration: BoxDecoration(
                    color: const Color(0xFFE2E8F0),
                    borderRadius: BorderRadius.circular(50),
                  ),
                  padding: const EdgeInsets.all(4),
                  child: Row(
                    children: [
                      GestureDetector(
                        onTap: () => setState(() => selectedTab = 0),
                        child: Container(
                          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
                          decoration: BoxDecoration(
                            color: selectedTab == 0 ? const Color(0xFF0A4FD6) : Colors.transparent,
                            borderRadius: BorderRadius.circular(50),
                          ),
                          child: Text(
                            "Upcoming",
                            style: TextStyle(
                              color: selectedTab == 0 ? Colors.white : const Color(0xFF64708A),
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ),
                      GestureDetector(
                        onTap: () => setState(() => selectedTab = 1),
                        child: Container(
                          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
                          decoration: BoxDecoration(
                            color: selectedTab == 1 ? const Color(0xFF0A4FD6) : Colors.transparent,
                            borderRadius: BorderRadius.circular(50),
                          ),
                          child: Text(
                            "History",
                            style: TextStyle(
                              color: selectedTab == 1 ? Colors.white : const Color(0xFF64708A),
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Expanded(
              child: filtered.isEmpty
                  ? Center(
                      child: Text(
                        selectedTab == 0 ? "No upcoming trips. Plan one from Explore!" : "No trip history yet.",
                        style: const TextStyle(color: Color(0xFF64708A)),
                      ),
                    )
                  : ListView.builder(
                      itemCount: filtered.length,
                      itemBuilder: (context, index) {
                        final trip = filtered[index];
                        final isExpanded = expandedTripIds.contains(trip.id);
                        return Card(
                          color: Colors.white,
                          elevation: 2,
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                          margin: const EdgeInsets.symmetric(vertical: 6),
                          child: Padding(
                            padding: const EdgeInsets.all(16),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Row(
                                  children: [
                                    Expanded(
                                      child: Text(trip.name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                                    ),
                                    IconButton(
                                      icon: Icon(isExpanded ? Icons.keyboard_arrow_up : Icons.keyboard_arrow_down, color: const Color(0xFF0A4FD6)),
                                      onPressed: () {
                                        setState(() {
                                          if (isExpanded) {
                                            expandedTripIds.remove(trip.id);
                                          } else {
                                            expandedTripIds.add(trip.id);
                                          }
                                        });
                                      },
                                    ),
                                    IconButton(
                                      icon: const Icon(Icons.close, color: Colors.red),
                                      onPressed: () async {
                                        await TripStore.delete(trip.id);
                                        _loadTrips();
                                      },
                                    ),
                                  ],
                                ),
                                const SizedBox(height: 4),
                                Text("📅 Date: ${trip.date}", style: const TextStyle(color: Color(0xFF0284C7), fontWeight: FontWeight.bold, fontSize: 13)),
                                Text(trip.meta, style: const TextStyle(color: Color(0xFF64708A), fontSize: 12)),
                                const SizedBox(height: 10),
                                if (selectedTab == 0)
                                  Row(
                                    children: [
                                      Expanded(
                                        child: OutlinedButton(
                                          onPressed: () {
                                            final destName = trip.destination.isNotEmpty ? trip.destination : "Taal Heritage Town";
                                            context.go('/itinerary/$destName/${trip.budget}/${trip.spotKeys}');
                                          },
                                          child: const Text("✏️ Edit Itinerary", style: TextStyle(color: Color(0xFF7C3AED), fontWeight: FontWeight.bold, fontSize: 12)),
                                        ),
                                      ),
                                    ],
                                  )
                                else
                                  ElevatedButton(
                                    onPressed: () async {
                                      final updated = trip.copyWith(done: false);
                                      await TripStore.update(updated);
                                      setState(() => selectedTab = 0);
                                      _loadTrips();
                                    },
                                    style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF16A34A)),
                                    child: const Text("🔄 Redo Trip & Move to Upcoming", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                                  ),
                              ],
                            ),
                          ),
                        );
                      },
                    ),
            ),
          ],
        ),
      ),
    );
  }
}
