import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../models/models.dart';

class DestinationDetailWidget extends StatefulWidget {
  final String destinationName;
  final Function(String, int) onSearchTrips;

  const DestinationDetailWidget({
    super.key,
    required this.destinationName,
    required this.onSearchTrips,
  });

  @override
  State<DestinationDetailWidget> createState() => _DestinationDetailWidgetState();
}

class _DestinationDetailWidgetState extends State<DestinationDetailWidget> {
  final TextEditingController budgetController = TextEditingController(text: "3,500");
  String selectedTier = "Standard";
  bool showGalleryModal = false;

  @override
  void dispose() {
    budgetController.dispose();
    super.dispose();
  }

  int get parsedBudget {
    final clean = budgetController.text.replaceAll(",", "");
    return int.tryParse(clean) ?? 0;
  }

  @override
  Widget build(BuildContext context) {
    final images = getDestinationImages(widget.destinationName);
    final allImages = [images.main, ...images.views];

    return Card(
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text("Destination in Batangas", style: TextStyle(color: Colors.grey, fontSize: 12)),
            const SizedBox(height: 8),
            // Main image card
            GestureDetector(
              onTap: () => setState(() => showGalleryModal = true),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(12),
                child: Stack(
                  alignment: Alignment.bottomCenter,
                  children: [
                    Image.asset(
                      images.main,
                      height: 180,
                      width: double.infinity,
                      fit: BoxFit.cover,
                    ),
                    if (allImages.length > 1)
                      Container(
                        margin: const EdgeInsets.all(8),
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                        decoration: BoxDecoration(
                          color: Colors.black.withValues(alpha: 0.65),
                          borderRadius: BorderRadius.circular(50),
                        ),
                        child: Text(
                          "Tap photo to view & swipe (${allImages.length} photos)",
                          style: const TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.w500),
                        ),
                      ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 14),
            Text(widget.destinationName, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
            const SizedBox(height: 4),
            Text(_destinationDesc(widget.destinationName), style: const TextStyle(color: Colors.darkGray, fontSize: 14)),
            const SizedBox(height: 16),
            const Divider(),
            const SizedBox(height: 12),
            const Text("Total budget (per person)", style: TextStyle(color: Colors.grey, fontSize: 12)),
            const SizedBox(height: 4),
            Text(
              "₱${NumberFormat("#,###").format(parsedBudget)}",
              style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold, color: Color(0xFF0A4FD6)),
            ),
            const SizedBox(height: 10),
            TextField(
              controller: budgetController,
              keyboardType: TextInputType.number,
              decoration: InputDecoration(
                filled: true,
                fillColor: Colors.white,
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
              ),
              onChanged: (v) => setState(() => selectedTier = ""),
            ),
            const SizedBox(height: 10),
            Row(
              children: [
                _tierButton("Budget", 1500),
                const SizedBox(width: 8),
                _tierButton("Standard", 3500),
                const SizedBox(width: 8),
                _tierButton("Luxury", 8000),
              ],
            ),
            const SizedBox(height: 14),
            SizedBox(
              width: double.infinity,
              height: 48,
              child: ElevatedButton(
                onPressed: () => widget.onSearchTrips(widget.destinationName, parsedBudget),
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF21C55E),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                ),
                child: const Text("Search Batangas Trips", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 15)),
              ),
            ),
            // Gallery Modal
            if (showGalleryModal)
              AlertDialog(
                title: Text(widget.destinationName),
                content: SizedBox(
                  width: double.maxFinite,
                  height: 260,
                  child: PageView.builder(
                    itemCount: allImages.length,
                    itemBuilder: (context, index) {
                      return Image.asset(allImages[index], fit: BoxFit.cover);
                    },
                  ),
                ),
                actions: [
                  TextButton(
                    onPressed: () => setState(() => showGalleryModal = false),
                    child: const Text("Close"),
                  ),
                ],
              ),
          ],
        ),
      ),
    );
  }

  Widget _tierButton(String tier, int amount) {
    final isSelected = selectedTier == tier;
    return Expanded(
      child: GestureDetector(
        onTap: () {
          setState(() {
            selectedTier = tier;
            budgetController.text = NumberFormat("#,###").format(amount);
          });
        },
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 10),
          decoration: BoxDecoration(
            color: isSelected ? const Color(0xFF0A4FD6) : const Color(0xFFF1F5F9),
            borderRadius: BorderRadius.circular(50),
            border: Border.all(color: isSelected ? Colors.transparent : const Color(0xFFC7D0E0)),
          ),
          alignment: Alignment.center,
          child: Text(
            tier,
            style: TextStyle(
              color: isSelected ? Colors.white : const Color(0xFF1E293B),
              fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
              fontSize: 13,
            ),
          ),
        ),
      ),
    );
  }

  String _destinationDesc(String name) {
    if (name.contains("Taal")) {
      return "Explore historical Taal Basilica, heritage Spanish colonial houses, and enjoy authentic Batangas kapeng barako.";
    }
    return "Discover this destination's sights, local eats, and hidden gems.";
  }
}
